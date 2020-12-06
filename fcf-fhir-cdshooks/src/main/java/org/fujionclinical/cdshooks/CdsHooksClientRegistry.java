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
import org.fujion.common.AbstractRegistry;
import org.fujion.common.Logger;
import org.fujionclinical.cdshooks.CdsHooksClient.InvocationRequest;
import org.fujionclinical.cdshooks.CdsHooksPreparedRequest.CdsHooksContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A registry of all CDS Hooks clients, indexed by discovery endpoint. This supports tracking CDS
 * Hooks services across multiple endpoints.
 */
public class CdsHooksClientRegistry extends AbstractRegistry<String, CdsHooksClient> {

    private static final Logger log = Logger.create(CdsHooksClientRegistry.class);

    private static final CdsHooksClientRegistry instance = new CdsHooksClientRegistry();

    /**
     * Factory method for creating clients from a comma-delimited list of endpoints.
     *
     * @param endpoints Comma-delimited list of endpoints.
     * @return Reference to the client registry.
     */
    public static CdsHooksClientRegistry createClients(String endpoints) {
        for (String ep: endpoints.split(",")) {
            ep = ep.trim();

            if (!ep.isEmpty()) {
                try {
                    instance.register(new CdsHooksClient(ep));
                    log.info("Registered CDS Hooks service at " + ep + ".");
                } catch (Exception e) {
                    log.error("Error registering CDS Hooks Service at " + ep + ".\nThe service will be unavailable.", e);
                }
            }
        }

        return instance;
    }

    public static CdsHooksClientRegistry getInstance() {
        return instance;
    }

    private CdsHooksClientRegistry() {
    }

    @Override
    protected String getKey(CdsHooksClient client) {
        return client.getDiscoveryEndpoint();
    }

    /**
     * Creates one invocation request per hook type per endpoint.
     *
     * @param fhirClient The FHIR client
     * @param hookType The hook type.
     * @param context The context map.
     * @param callback Method to call as each request completes.
     * @return A list of created invocation requests.
     */
    public List<InvocationRequest> createInvocationRequests(IGenericClient fhirClient, String hookType, CdsHooksContext context, Consumer<InvocationRequest> callback) {
        List<InvocationRequest> requests = new ArrayList<>();

        for (CdsHooksClient client : this) {
            InvocationRequest invocationRequest = client.createInvocationRequest(fhirClient, hookType, context, callback);
            requests.add(invocationRequest);
        }

        return requests;
    }
}
