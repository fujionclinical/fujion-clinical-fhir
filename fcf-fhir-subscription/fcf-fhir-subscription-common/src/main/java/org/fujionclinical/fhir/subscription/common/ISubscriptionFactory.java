package org.fujionclinical.fhir.subscription.common;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.model.ConceptCode;

public interface ISubscriptionFactory {

    BaseSubscriptionWrapper create(
            IGenericClient client,
            String paramIndex,
            String callbackUrl,
            ResourceSubscriptionService.PayloadType payloadType,
            String criteria,
            ConceptCode tag);
}
