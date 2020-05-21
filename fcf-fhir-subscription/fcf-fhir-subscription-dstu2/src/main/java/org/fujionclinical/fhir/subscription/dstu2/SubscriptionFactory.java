package org.fujionclinical.fhir.subscription.dstu2;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.Subscription;
import ca.uhn.fhir.model.dstu2.valueset.SubscriptionChannelTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.SubscriptionStatusEnum;
import ca.uhn.fhir.rest.api.PreferReturnEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.model.ConceptCode;
import org.fujionclinical.fhir.subscription.common.BaseSubscriptionWrapper;
import org.fujionclinical.fhir.subscription.common.ISubscriptionFactory;
import org.fujionclinical.fhir.subscription.common.ResourceSubscriptionService;

import java.util.Collections;

public class SubscriptionFactory implements ISubscriptionFactory {
    @Override
    public BaseSubscriptionWrapper create(
            IGenericClient client,
            String paramIndex,
            String callbackUrl,
            ResourceSubscriptionService.PayloadType payloadType,
            String criteria,
            ConceptCode tag) {
        Subscription subscription = new Subscription();
        SubscriptionWrapper wrapper = new SubscriptionWrapper(subscription, paramIndex);
        Subscription.Channel channel = new Subscription.Channel();
        channel.setType(SubscriptionChannelTypeEnum.REST_HOOK);
        channel.setEndpoint(callbackUrl + wrapper.getSubscriptionId());
        channel.setPayload(payloadType.toString());
        subscription.setCriteria(criteria);
        subscription.setReason("Fujion Subscriber");
        subscription.setChannel(channel);
        subscription.setStatus(SubscriptionStatusEnum.REQUESTED);
        subscription.setTag(Collections.singletonList(new CodingDt(tag.getSystem(), tag.getCode())));
        subscription = (Subscription) client.create().resource(subscription).prefer(PreferReturnEnum.REPRESENTATION)
                .execute().getResource();
        return wrapper;
    }
}
