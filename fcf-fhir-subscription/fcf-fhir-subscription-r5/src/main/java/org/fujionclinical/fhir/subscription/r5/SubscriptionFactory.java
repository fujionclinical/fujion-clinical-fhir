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
package org.fujionclinical.fhir.subscription.r5;

import org.clinicalontology.terminology.impl.model.ConceptImpl;
import org.coolmodel.mediator.fhir.r5.common.R5Utils;
import org.fujionclinical.fhir.subscription.common.BaseSubscriptionFactory;
import org.fujionclinical.fhir.subscription.common.BaseSubscriptionWrapper;
import org.fujionclinical.fhir.subscription.common.ResourceSubscriptionService;
import org.hl7.fhir.r5.model.Subscription;

public class SubscriptionFactory extends BaseSubscriptionFactory {

    public SubscriptionFactory() {
        this(R5Utils.getModelDescriptor().getId());
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
            ConceptImpl tag) {
        Subscription subscription = new Subscription();
        SubscriptionWrapper wrapper = new SubscriptionWrapper(subscription, paramIndex, getDataSource());
        /* TODO:
        Subscription.SubscriptionChannelComponent channel = new Subscription.SubscriptionChannelComponent();
        channel.setType(Subscription.SubscriptionChannelType.RESTHOOK);
        channel.setEndpoint(callbackUrl + wrapper.getSubscriptionId());
        channel.setPayload(payloadType.toString());
        subscription.setCriteria(criteria);
        subscription.setReason("Fujion Subscriber");
        subscription.setChannel(channel);
        subscription.setStatus(Subscription.SubscriptionStatus.REQUESTED);
        subscription.getMeta().addTag(tag.getSystem(), tag.getCode(), tag.getText());
        subscription = (Subscription) client.create().resource(subscription).prefer(PreferReturnEnum.REPRESENTATION)
                .execute().getResource();
         */
        return wrapper.initialize();
    }

}
