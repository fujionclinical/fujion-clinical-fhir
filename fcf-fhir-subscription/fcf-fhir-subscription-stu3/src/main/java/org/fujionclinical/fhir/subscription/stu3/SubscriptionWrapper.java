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
package org.fujionclinical.fhir.subscription.stu3;

import org.fujionclinical.fhir.subscription.common.BaseSubscriptionWrapper;
import org.hl7.fhir.dstu3.model.Subscription;

/**
 * Wraps a FHIR subscription resource, adding necessary metadata for managing the subscription.
 */
public class SubscriptionWrapper extends BaseSubscriptionWrapper<Subscription> {

    /**
     * Create the subscription wrapper.
     *
     * @param paramIndex The index for looking up by criteria/payload type.
     */
    /*package*/ SubscriptionWrapper(Subscription subscription, String paramIndex) {
        super(subscription, paramIndex);
    }

    /**
     * Return the subscription criteria.
     *
     * @return The subscription criteria.
     */
    public String getCriteria() {
        return getSubscription().getCriteria();
    }

}
