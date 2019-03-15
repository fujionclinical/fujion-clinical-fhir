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
package org.fujionclinical.fhir.dstu3.api.security;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import org.fujionclinical.api.spring.PropertyBasedConfigurator;
import org.fujionclinical.fhir.common.client.IAuthInterceptor;

import java.io.IOException;

/**
 * Abstract base class for implementing authentication interceptors.
 */
public abstract class AbstractAuthInterceptor implements IAuthInterceptor {

    private final String authType;
    
    /**
     * Create the interceptor with the specified authorization type.
     *
     * @param parentConfigurator The client configurator.
     * @param authType The authorization type.
     */
    protected AbstractAuthInterceptor(PropertyBasedConfigurator parentConfigurator, String authType) {
        this.authType = authType.trim();
        parentConfigurator.wireParams(this);
    }
    
    /**
     * Intercepts the request, adding the appropriate authorization header.
     */
    @Override
    public void interceptRequest(IHttpRequest theRequest) {
        String credentials = getCredentials();
        
        if (credentials != null && !credentials.isEmpty()) {
            theRequest.addHeader(Constants.HEADER_AUTHORIZATION, authType + " " + credentials);
        }
    }
    
    @Override
    public void interceptResponse(IHttpResponse theResponse) throws IOException {
        // nothing
    }

}
