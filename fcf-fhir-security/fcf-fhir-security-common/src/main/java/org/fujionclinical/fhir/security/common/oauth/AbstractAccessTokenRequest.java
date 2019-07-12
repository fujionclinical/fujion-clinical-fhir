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
package org.fujionclinical.fhir.security.common.oauth;

import org.apache.commons.lang3.Validate;
import org.fujionclinical.fhir.security.common.ICredentialProvider;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAccessTokenRequest<T extends ICredentialProvider> implements org.fujionclinical.fhir.security.common.oauth.IAccessTokenRequest {

    private final Map<String, String> tokenRequestParams = new HashMap<>();

    private String clientId;

    private T credentials;

    protected AbstractAccessTokenRequest(String clientId, T credentials, org.fujionclinical.fhir.security.common.oauth.AccessTokenGrantType grantType) {
        Validate.notNull(clientId, "ClientId must not be null");
        Validate.notNull(credentials, "ICredentialProvider must not be null");
        Validate.notNull(grantType, "GrantType must not be null");

        this.tokenRequestParams.put("grant_type", grantType.getParamValue());
        this.clientId = clientId;
        this.credentials = credentials;
    }

    public String getClientId() {
        return clientId;
    }

    public T getCredentials() {
        return credentials;
    }

    @Override
    public Map<String, String> getParameters() {
        return this.getMergedParameters();
    }

    private Map<String, String> getMergedParameters() {
        Map<String, String> mergedParams = new HashMap<>(this.tokenRequestParams);
        Map<String, String> additionalParams = getAdditionalParameters();
        if (additionalParams != null) {
            mergedParams.putAll(getAdditionalParameters());
        }
        return mergedParams;
    }

    public abstract Map<String, String> getAdditionalParameters();

}
