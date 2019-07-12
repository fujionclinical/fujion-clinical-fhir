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
package org.fujionclinical.fhir.subscription.dstu2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoint for receiving subscription notifications from a FHIR server. Use instead of
 * ResourceSubscriptionServlet when using Spring MVC in the web stack.
 */
@RestController
@RequestMapping("/publish")
public class ResourceSubscriptionEndpoint {
    
    private static final Log log = LogFactory.getLog(ResourceSubscriptionEndpoint.class);

    private final ResourceSubscriptionService service;
    
    private boolean disabled;

    public ResourceSubscriptionEndpoint(ResourceSubscriptionService service) {
        this.service = service;
        disabled = service.isDisabled();
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> post(@PathVariable("id") String id, @RequestBody(required = false) String payload) {
        return processRequest(id, payload);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> put(@PathVariable("id") String id, @RequestBody(required = false) String payload) {
        return processRequest(id, payload);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return processRequest(id, null);
    }
    
    private ResponseEntity<?> processRequest(String id, String payload) {
        boolean rejected = disabled;
        
        if (!rejected) {
            try {
                rejected = !service.notifySubscribers(id, payload);
            } catch (Exception e) {
                rejected = true;
                disableService("notifying subscribers of a resource subscription event", e);
            }
        }

        return ResponseEntity.status(rejected ? 410 : 200).build();
    }

    /**
     * Log an exception and disable the endpoint.
     *
     * @param message Description of activity at the time of the exception.
     * @param e The exception.
     */
    private void disableService(String message, Exception e) {
        disabled = true;
        log.error("An exception occurred while " + message + ".\nTherefore, this service will be disabled", e);
    }
}
