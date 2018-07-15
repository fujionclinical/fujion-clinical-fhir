/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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
package org.fujionclinical.fhir.api.client;

import org.apache.http.client.HttpClient;

import ca.uhn.fhir.context.FhirVersionEnum;

/**
 * Subclasses FhirContext to allow custom RestfulClientFactory and to support various authentication
 * mechanisms.
 */
public class FhirContext extends ca.uhn.fhir.context.FhirContext {

    private final IFhirContextConfigurator config;

    private RestfulClientFactory myRestfulClientFactory;

    /**
     * @deprecated Use configurator-based constructor.
     */
    @Deprecated
    public FhirContext() {
        this((FhirVersionEnum) null);
    }

    public FhirContext(IFhirContextConfigurator config) {
        super(config.getVersion());
        this.config = config;
    }

    public FhirContext(FhirVersionEnum version) {
        super(version);
        config = null;
    }

    /**
     * Overridden to create a custom RESTful client factory.
     */
    @Override
    public RestfulClientFactory getRestfulClientFactory() {
        if (myRestfulClientFactory == null) {
            myRestfulClientFactory = new RestfulClientFactory(this);

            if (config != null) {
                configureClientFactory();
            }

        }

        return myRestfulClientFactory;
    }

    private void configureClientFactory() {
        String proxy = config.getProxy();

        if (proxy != null) {
            String[] pcs = proxy.split("\\:", 2);
            myRestfulClientFactory.setProxy(pcs[0], Integer.parseInt(pcs[1]));
        }
        
        myRestfulClientFactory.setConnectionRequestTimeout(config.getConnectionRequestTimeout());
        myRestfulClientFactory.setConnectTimeout(config.getConnectTimeout());
        myRestfulClientFactory.setPoolMaxPerRoute(config.getPoolMaxPerRoute());
        myRestfulClientFactory.setPoolMaxTotal(config.getPoolMaxTotal());
        myRestfulClientFactory.setSocketTimeout(config.getSocketTimeout());
        myRestfulClientFactory.setServerValidationMode(config.getServerValidationMode());
    }
    
    /**
     * Supports registering special http clients to handle requests based on URL patterns.
     *
     * @param pattern Url pattern
     * @param client The http client.
     */
    public void registerHttpClient(String pattern, HttpClient client) {
        getRestfulClientFactory().getNativeHttpClient().registerHttpClient(pattern, client);
    }

}
