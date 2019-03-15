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
package org.fujionclinical.fhir.api.subscription;

import org.fujionclinical.api.event.IEventManager;
import org.fujionclinical.fhir.api.subscription.ResourceSubscriptionService.PayloadType;

/**
 * Convenience class for managing resource subscriptions at the application instance level. Simply
 * delegates resource subscription requests to the subscription service and callback registrations
 * to the event manager for the application instance.
 */
public class ResourceSubscriptionManager {
    
    private final IEventManager eventManager;

    private final ResourceSubscriptionService service;
    
    public ResourceSubscriptionManager(IEventManager eventManager, ResourceSubscriptionService service) {
        this.eventManager = eventManager;
        this.service = service;
    }
    
    public boolean isDisabled() {
        return service.isDisabled();
    }
    
    public SubscriptionWrapper subscribe(String criteria, ISubscriptionCallback callback) {
        return subscribe(criteria, null, callback);
    }

    public SubscriptionWrapper subscribe(String criteria, PayloadType payloadType, ISubscriptionCallback callback) {
        SubscriptionWrapper subscription = service.subscribe(criteria, payloadType);
        
        if (subscription != null) {
            eventManager.subscribe(subscription.getEventName(), callback);
        }
        
        return subscription;
    }
    
    public SubscriptionWrapper unsubscribe(SubscriptionWrapper subscription, ISubscriptionCallback callback) {
        if (subscription != null) {
            eventManager.unsubscribe(subscription.getEventName(), callback);
            service.unsubscribe(subscription);
        }

        return subscription;
    }
    
}
