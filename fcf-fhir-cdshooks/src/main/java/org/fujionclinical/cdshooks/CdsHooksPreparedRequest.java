/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.cdshooks;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.UrlUtil;
import org.fujionclinical.cdshooks.CdsHooksPlaceholderResolvers.ICdsHooksPlaceholderResolver;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.opencds.hooks.lib.json.JsonUtil;
import org.opencds.hooks.model.context.WritableHookContext;
import org.opencds.hooks.model.discovery.Service;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.hooks.model.request.WritableCdsRequest;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * Wraps a CDS Hooks request that is fully prepared for invocation. A prepared request may be
 * invoked multiple times - each request will be given a unique instance id.
 */
public class CdsHooksPreparedRequest {

    public static class CdsHooksContext extends HashMap<String, String> {}

    private static final CdsHooksPlaceholderResolvers resolvers = CdsHooksPlaceholderResolvers.getInstance();

    private final CdsRequest request;

    private final Service service;

    private final JsonUtil jsonUtil;

    protected CdsHooksPreparedRequest(IGenericClient fhirClient, Service service, CdsHooksContext context) {
        jsonUtil = CdsHooksUtil.getJsonUtil(fhirClient);
        this.service = service;
        request = buildRequest(service, context.get("userId"));
        request.setContext(createHookContext(context));
        processPrefetch(fhirClient, service);
    }

    /**
     * Returns the body of the request (in JSON format) to be passed in a POST invocation. A unique
     * instance id is generated for each call.
     *
     * @return The body of the request (in JSON format) to be passed in a POST invocation.
     */
    protected synchronized String getBody() {
        request.setHookInstance(UUID.randomUUID().toString());
        return jsonUtil.toJson(request);
    }

    /**
     * Returns the associated CDS Hooks service.
     *
     * @return The associated CDS Hooks service.
     */
    protected Service getService() {
        return service;
    }

    /**
     * Creates a hook context from a context map.
     *
     * @param hooksContext The context map.
     * @return A hook context derived from the context map.
     */
    private WritableHookContext createHookContext(CdsHooksContext hooksContext) {
        WritableHookContext hookContext = new WritableHookContext();

        if (hooksContext != null) {
            for (Entry<String, String> entry : hooksContext.entrySet()) {
                hookContext.add(entry.getKey(), entry.getValue());
            }
        }

        return hookContext;
    }

    /**
     * Build a CDS request instance.
     *
     * @param service The CDS Hooks service.
     * @param userId The user id.
     * @return A minimal CDS request instance.
     */
    private CdsRequest buildRequest(Service service, String userId) {
        CdsRequest request = new WritableCdsRequest();
        request.setHook(service.getHook());
        // request.setUser(userId);
        return request;
    }

    /**
     * Process all pre-fetch entries defined in a service and store in the request.
     *
     * @param fhirClient The FHIR client to use to resolve pre-fetch entries.
     * @param service The service defining the resources to fetch.
     */
    private void processPrefetch(IGenericClient fhirClient, Service service) {
        Map<String, String> prefetch = service.getPrefetch();
        
        for (Entry<String, String> entry : prefetch.entrySet()) {
            String key = entry.getKey();
            String query = replacePlaceholders(entry.getValue());
            UrlUtil.UrlParts parts = UrlUtil.parseUrl(query);
            String resourceId = parts.getResourceId();
            IBaseResource resource;

            if (resourceId == null || resourceId.isEmpty()) {
                resource = fhirClient.search().byUrl(query).execute();
            } else {
                resource = fhirClient.read().resource(parts.getResourceType()).withUrl(query).execute();
            }

            request.addPrefetchResource(key, resource);
        }
    }

    /**
     * Replace placeholders within a pre-fetch entry.
     *
     * @param value The initial value of the pre-fetch entry.
     * @return The pre-fetch entry with all placeholders resolved.
     */
    private String replacePlaceholders(String value) {
        int i = 0;
        
        while ((i = value.indexOf("{{", i)) >= 0) {
            int j = value.indexOf("}}", i);

            if (j < 0) {
                break;
            }

            int l = value.length();
            String paramstr = resolvePlaceholder(value.substring(i + 2, j));
            j += 2;
            value = value.substring(0, i) + paramstr + value.substring(j);
            i += j + value.length() - l;
        }

        return value;
    }
    
    /**
     * Resolve a parameter from the given context.
     *
     * @param param The qualified placeholder to resolve (e.g., context.patientId).
     * @return The resolved placeholder value.
     */
    private String resolvePlaceholder(String param) {
        String[] pcs = param.split("\\.", 2);
        String type = pcs[0];
        String placeholder = pcs.length == 1 ? null : pcs[1];
        ICdsHooksPlaceholderResolver resolver = placeholder == null ? null : resolvers.get(type);
        Assert.notNull(resolver, () -> "Unresolvable placeholder '" + param + "'");
        String value = resolver.resolve(placeholder, request);
        return value == null ? "" : value;
    }
    
}
