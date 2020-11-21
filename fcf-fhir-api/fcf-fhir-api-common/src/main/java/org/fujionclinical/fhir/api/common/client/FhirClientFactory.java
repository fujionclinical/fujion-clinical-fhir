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
package org.fujionclinical.fhir.api.common.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import org.fujionclinical.fhir.security.common.IAuthInterceptor;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class FhirClientFactory {

    private static FhirClientFactory instance;

    private final Map<String, IGenericClient> registry = new HashMap<>();

    private final FhirContext fhirContext;

    public static FhirClientFactory getInstance() {
        return instance;
    }

    public FhirClientFactory(FhirContext fhirContext) {
        Assert.state(instance == null, "Attempt to create a second instance of FhirClientFactory");
        instance = this;
        this.fhirContext = fhirContext;
    }

    public IGenericClient getClient(String qualifier) {
        return getClient(qualifier, false);
    }

    public IGenericClient getClient(
            String qualifier,
            boolean acceptDefault) {
        IGenericClient client = registry.get(qualifier);
        return acceptDefault && client == null ? registry.get("") : client;
    }

    /**
     * Creates a generic client.
     *
     * @param config A FHIR configurator.
     * @return The newly created generic client.
     */
    public IGenericClient createClient(IFhirClientConfigurator config) {
        String qualifier = config.getQualifier();
        Assert.isTrue(!registry.containsKey(qualifier), () -> "A FHIR client qualifier named '" + qualifier + "' already exists");
        IGenericClient client = fhirContext.newRestfulGenericClient(config.getServerBase());

        if (client instanceof GenericClient) {
            ((GenericClient) client).setDontValidateConformance(!config.isValidateConformance());
        }

        IAuthInterceptor authInterceptor = config.getAuthInterceptor();

        if (authInterceptor != null) {
            client.registerInterceptor(authInterceptor);
        }

        client.setPrettyPrint(config.isPrettyPrint());
        client.setEncoding(config.getEncoding());
        client.setSummary(config.getSummary());
        registry.put(qualifier, client);
        return client;
    }

}
