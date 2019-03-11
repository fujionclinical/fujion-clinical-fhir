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
package org.fujionclinical.fhir.api.smart;

import org.springframework.beans.factory.annotation.Value;

import org.fujionclinical.fhir.api.smart.SmartContextBase.ContextMap;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation for binding SMART context to launch id.
 */
public class SmartContextBinder implements ISmartContextBinder {

    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${smart.service.launch.binder.url:}")
    private String smartLaunchBinder;
    
    public SmartContextBinder() {
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

        String launchId = UUID.randomUUID().toString();
        ContextMap cloneMap = new ContextMap(contextMap);
        cloneMap.put("launch_id", launchId);
        Map<String, Object> body = new HashMap<>();
        body.put("parameters", cloneMap);
        restTemplate.postForObject(smartLaunchBinder, body, String.class);
        return launchId;
    }
    
}
