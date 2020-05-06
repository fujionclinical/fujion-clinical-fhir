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
package org.fujionclinical.fhir.smart.launch.oidc;

import org.apache.http.impl.client.HttpClientBuilder;
import org.fujionclinical.fhir.smart.common.ISmartContextBinder;
import org.fujionclinical.fhir.smart.common.SmartContextBase.ContextMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OpenID Connect implementation for binding SMART context to launch id.
 */
public class SmartContextBinder implements ISmartContextBinder {

    private final RestTemplate restTemplate;

    private final HttpHeaders headers = new HttpHeaders();

    @Value("${smart.service.launch.binder.url:}")
    private String smartLaunchBinder;

    @Value("${smart.service.launch.binder.username:}")
    private String username;

    @Value("${smart.service.launch.binder.password:}")
    private String password;

    public SmartContextBinder() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        restTemplate = new RestTemplate(clientHttpRequestFactory);
    }

    private void init() {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (!username.isEmpty() && !password.isEmpty()) {
            headers.setBasicAuth(username, password);
        }
    }

    /**
     * Binds the context to a unique launch identifier.
     * 
     * @param contextMap The context map.
     * @return A unique launch identifier.
     */
    @Override
    public String bindContext(ContextMap contextMap) {
        if (smartLaunchBinder.isEmpty()) {
            return null;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("parameters", contextMap);
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        Map<String, String> result = restTemplate.postForObject(smartLaunchBinder, entity, Map.class);
        return result.get("launch_id");
    }
    
}
