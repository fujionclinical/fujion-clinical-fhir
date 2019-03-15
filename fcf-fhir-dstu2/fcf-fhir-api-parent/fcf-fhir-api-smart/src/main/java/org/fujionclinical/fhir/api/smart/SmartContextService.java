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

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.common.QueryStringBuilder;
import org.fujionclinical.fhir.api.smart.SmartContextBase.ContextMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 * Provides full launch url for SMART apps.
 */
public class SmartContextService {
    
    private static final Log log = LogFactory.getLog(SmartContextService.class);

    private static final SmartContextService instance = new SmartContextService();
    
    @Value("${smart.service.root.url:}")
    private String smartServiceRoot;
    
    @Value("${fhir.client.root.url:}")
    private String fhirServiceRoot;
    
    private String serviceRoot;

    private ISmartContextBinder smartContextBinder;

    public static SmartContextService getInstance() {
        return instance;
    }
    
    private SmartContextService() {
    }
    
    public boolean isAvailable() {
        return serviceRoot != null;
    }

    public String getUrl(SmartManifest manifest, Collection<ContextMap> contexts) {
        if (contexts == null || contexts.isEmpty()) {
            return null;
        }
        
        String qs = getQueryString(contexts);
        return qs.isEmpty() ? null : manifest.getValue("launch_uri") + "?" + qs;
    }

    public void setSmartContextBinder(ISmartContextBinder smartContextBinder) {
        this.smartContextBinder = smartContextBinder;
    }

    /**
     * Return query string for the SMART plugin.
     *
     * @param contexts The relevant contexts.
     * @return The query string.
     */
    private String getQueryString(Collection<ContextMap> contexts) {
        QueryStringBuilder qs = new QueryStringBuilder();
        ContextMap combinedContexts = new ContextMap();

        for (ContextMap context : contexts) {
            combinedContexts.putAll(context);

            for (Entry<String, String> entry : context.entrySet()) {
                qs.append(entry.getKey(), entry.getValue());
            }
        }
        
        if (qs.length() > 0) {
            qs.append("iss", serviceRoot);
            qs.append("launch", smartContextBinder.bindContext(combinedContexts));
        }
        
        return qs.toString();
    }
    
    public void init() {
        serviceRoot = StringUtils.isEmpty(smartServiceRoot) ? fhirServiceRoot : smartServiceRoot;
        serviceRoot = StringUtils.trimToNull(StringUtils.chomp(serviceRoot, "/"));

        if (serviceRoot == null) {
            log.warn("No service root url defined for SMART.  SMART services will be unavailable.");
        } else if (smartContextBinder == null) {
            serviceRoot = null;
            log.warn("No SMART context binder was specified.  SMART services will be unavailable.");
        }
    }
    
}
