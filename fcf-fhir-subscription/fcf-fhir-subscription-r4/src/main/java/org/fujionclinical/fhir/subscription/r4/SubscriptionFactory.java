package org.fujionclinical.fhir.subscription.r4;

import ca.uhn.fhir.rest.api.PreferReturnEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.model.ConceptCode;
import org.fujionclinical.fhir.subscription.common.BaseSubscriptionWrapper;
import org.fujionclinical.fhir.subscription.common.ISubscriptionFactory;
import org.fujionclinical.fhir.subscription.common.ResourceSubscriptionService;
import org.hl7.fhir.r4.model.Subscription;

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
        return wrapper;
    }
}
