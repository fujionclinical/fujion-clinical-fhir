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
package org.fujionclinical.fhir.subscription.r4;

import edu.utah.kmm.model.cool.mediator.fhir.r4.common.R4Utils;
import org.fujionclinical.fhir.subscription.common.BaseSubscriptionFactory;
import org.fujionclinical.fhir.subscription.common.BaseSubscriptionWrapper;
import org.fujionclinical.fhir.subscription.common.ResourceSubscriptionService;
import org.hl7.fhir.r4.model.Subscription;
import org.opencds.tools.terminology.api.model.ConceptReferenceImpl;

public class SubscriptionFactory extends BaseSubscriptionFactory {

    public SubscriptionFactory() {
        this(R4Utils.FHIR_R4.getId());
    }

    public SubscriptionFactory(String dataSourceId) {
        super(dataSourceId);
    }

    @Override
    public BaseSubscriptionWrapper create(
            String paramIndex,
            String callbackUrl,
            ResourceSubscriptionService.PayloadType payloadType,
            String criteria,
            ConceptReferenceImpl tag) {
        Subscription subscription = new Subscription();
        SubscriptionWrapper wrapper = new SubscriptionWrapper(subscription, paramIndex, getDataSource());
        Subscription.SubscriptionChannelComponent channel = new Subscription.SubscriptionChannelComponent();
        channel.setType(Subscription.SubscriptionChannelType.RESTHOOK);
        channel.setEndpoint(callbackUrl + wrapper.getSubscriptionId());
        channel.setPayload(payloadType.toString());
        subscription.setCriteria(criteria);
        subscription.setReason("Fujion Subscriber");
        subscription.setChannel(channel);
        subscription.setStatus(Subscription.SubscriptionStatus.REQUESTED);
        subscription.getMeta().addTag(tag.getCodeSystemAsString(), tag.getCode(), tag.getPreferredName());
        return wrapper.initialize();
    }

}
