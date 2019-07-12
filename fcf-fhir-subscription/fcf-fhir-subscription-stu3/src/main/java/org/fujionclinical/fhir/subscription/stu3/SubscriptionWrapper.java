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
package org.fujionclinical.fhir.subscription.stu3;

import org.fujionclinical.fhir.subscription.stu3.ResourceSubscriptionService.PayloadType;
import org.hl7.fhir.dstu3.model.Subscription;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Wraps a FHIR subscription resource, adding necessary metadata for managing the subscription.
 */
public class SubscriptionWrapper {
    
    public static final String EVENT_ROOT = "FHIR.SUB";
    
    private Subscription subscription;

    private final String paramIndex;
    
    private final String subscriptionId;
    
    private int refCount;
    
    protected static String getParamIndexKey(String criteria, PayloadType payloadType) {
        return payloadType.name() + "|" + criteria;
    }

    /**
     * Create the subscription wrapper.
     *
     * @param paramIndex The index for looking up by criteria/payload type.
     */
    /*package*/ SubscriptionWrapper(String paramIndex) {
        this.paramIndex = paramIndex;
        this.subscriptionId = UUID.randomUUID().toString();
    }
    
    /**
     * Return the unique subscription id.
     *
     * @return Unique subscription id.
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }
    
    /**
     * Return the subscription criteria.
     *
     * @return The subscription criteria.
     */
    public String getCriteria() {
        return subscription.getCriteria();
    }

    /**
     * Return the name of the event used for callbacks.
     *
     * @return Event name used for callbacks.
     */
    public String getEventName() {
        return EVENT_ROOT + "." + subscriptionId;
    }

    /**
     * Returns the wrapped subscription resource.
     *
     * @return The wrapped subscription resource.
     */
    protected Subscription getSubscription() {
        return subscription;
    }
    
    /**
     * Sets the wrapped subscription resource.
     *
     * @param subscription The FHIR subscription.
     */
    protected void setSubscription(Subscription subscription) {
        Assert.isNull(this.subscription, "Wrapped subscription may not be changed.");
        this.subscription = subscription;
    }
    
    /**
     * Returns the key that is a combination of the payload type and the criteria.
     *
     * @return Key for the parameter-based index.
     */
    protected String getParamIndex() {
        return paramIndex;
    }

    /**
     * Decrements the reference count.
     *
     * @return The updated reference count.
     */
    protected int decRefCount() {
        return refCount == 0 ? 0 : --refCount;
    }

    /**
     * Increments the reference count.
     *
     * @return The updated reference count.
     */
    protected int incRefCount() {
        return ++refCount;
    }
}
