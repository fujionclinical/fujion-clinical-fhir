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

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.PreferReturnEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.event.EventMessage;
import org.fujionclinical.api.event.EventUtil;
import org.fujionclinical.api.messaging.Message;
import org.fujionclinical.api.messaging.ProducerService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Subscription;
import org.hl7.fhir.r4.model.Subscription.SubscriptionChannelComponent;
import org.hl7.fhir.r4.model.Subscription.SubscriptionChannelType;
import org.hl7.fhir.r4.model.Subscription.SubscriptionStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing FHIR resource subscriptions. It provides a bridge between FHIR's
 * subscription framework and CWF's event framework. Each registered FHIR subscription is indexed by
 * a unique key and by its associated criteria. Each subscription is associated with a unique event
 * name. When the application receives a subscription notification from the FHIR server via one of
 * the supported mechanisms (preferably via a REST callback), this service may be invoked to deliver
 * the notification to each of the CWF subscribers by means of the associated event name. The
 * service also manages the lifecycle of the subscriptions, creating and revoking them as required.
 */
public class ResourceSubscriptionService {

    public enum PayloadType {
        NONE(null), XML("application/fhir+xml"), JSON("application/fhir+json");
        
        private String mimeType;

        PayloadType(String mimeType) {
            this.mimeType = mimeType;
        }
    }
    
    private final Log log = LogFactory.getLog(ResourceSubscriptionService.class);

    private final IGenericClient client;

    private final ProducerService producer;

    private final boolean disabled;

    private final String callbackUrl;

    private final Coding subscriptionTag;
    
    private final Map<String, SubscriptionWrapper> subscriptionsByParams = new HashMap<>();

    private final Map<String, SubscriptionWrapper> subscriptionsById = new HashMap<>();

    /**
     * Create the resource subscription service.
     *
     * @param client The FHIR client for managing subscription requests.
     * @param producer The message producer for delivering events to subscribers.
     * @param callbackUrl The callback URL to be associated with new subscriptions. If no callback
     *            URL is specified, this service will be disabled.
     */
    public ResourceSubscriptionService(IGenericClient client, ProducerService producer, String callbackUrl) {
        this.client = client;
        this.producer = producer;
        disabled = StringUtils.isEmpty(callbackUrl);
        this.callbackUrl = disabled ? null : callbackUrl.endsWith("/") ? callbackUrl : callbackUrl + "/";
        subscriptionTag = new Coding();
        subscriptionTag.setSystem(callbackUrl);
        subscriptionTag.setCode("ResourceSubscription");
        destroy();
        log.info("FHIR Resource Subscription Service is " + (disabled ? "disabled." : "enabled."));
    }

    /**
     * Delete any old subscriptions upon startup/shutdown.
     */
    public void destroy() {
        if (!disabled) {
            try {
                ICriterion<?> criterion = new TokenClientParam("_tag").exactly().systemAndCode(subscriptionTag.getSystem(),
                    subscriptionTag.getCode());
                client.delete().resourceConditionalByType(Subscription.class).where(criterion).execute();
            } catch (Exception e) {
                log.error("Error attempting to delete old subscription resources", e);
            }
        }
    }

    /**
     * Returns true if the service is disabled.
     *
     * @return True if the service is disabled.
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Associate a CWF event with a FHIR subscription (creating a new FHIR subscription as
     * necessary).
     *
     * @param criteria The subscription criteria (see FHIR specification).
     * @return The subscription wrapper.
     */
    public synchronized SubscriptionWrapper subscribe(String criteria) {
        return subscribe(criteria, null);
    }

    /**
     * Associate a CWF event with a FHIR subscription (creating a new FHIR subscription as
     * necessary).
     *
     * @param criteria The subscription criteria (see FHIR specification).
     * @param payloadType The expected type of the payload.
     * @return The subscription wrapper.
     */
    public synchronized SubscriptionWrapper subscribe(String criteria, PayloadType payloadType) {
        return disabled ? null : getOrCreateSubscription(criteria, payloadType == null ? PayloadType.NONE : payloadType);
    }

    /**
     * Unsubscribe from a FHIR subscription (revoking the FHIR subscription if there are no further
     * references).
     *
     * @param wrapper The subscription wrapper.
     * @return The subscription wrapper.
     */
    public synchronized SubscriptionWrapper unsubscribe(SubscriptionWrapper wrapper) {
        if (wrapper != null && wrapper.decRefCount() == 0) {
            deleteSubscription(wrapper);
        }

        return wrapper;
    }

    /**
     * Unsubscribe from multiple FHIR subscriptions.
     *
     * @param wrappers Collection of subscription wrappers.
     */
    public synchronized void unsubscribe(Collection<SubscriptionWrapper> wrappers) {
        if (wrappers != null) {
            for (SubscriptionWrapper wrapper : wrappers) {
                unsubscribe(wrapper);
            }
        }
    }

    /**
     * Notify all event subscribers of a subscription notification.
     *
     * @param id Unique identifier of the FHIR subscription.
     * @param payload Serialized resource (may be null).
     * @return True if the subscription notification was delivered.
     */
    protected synchronized boolean notifySubscribers(String id, String payload) {
        SubscriptionWrapper wrapper = subscriptionsById.get(id);
        boolean found = wrapper != null;

        if (found) {
            IBaseResource resource = parseResource(payload);
            String eventName = wrapper.getEventName();
            Message message = new EventMessage(eventName, resource);
            producer.publish(EventUtil.getChannelName(eventName), message);
        }

        return found;
    }

    /**
     * Parses a resource from the raw payload.
     *
     * @param payload Serialized form of the resource (may be null).
     * @return The parsed resource (may be null).
     */
    private IBaseResource parseResource(String payload) {
        IBaseResource resource = null;
        payload = StringUtils.trimToNull(payload);
        
        if (payload != null) {
            IParser parser = payload.startsWith("{") ? client.getFhirContext().newJsonParser()
                    : client.getFhirContext().newXmlParser();
            
            try {
                resource = parser.parseResource(payload);
            } catch (Exception e) {
                log.error("Unable to parse payload in subscription request", e);
            }
        }
        
        return resource;
    }

    /**
     * Returns a FHIR subscription wrapper from the list of active subscriptions, creating one if it
     * does not exist.
     *
     * @param criteria The subscription criteria.
     * @param payloadType The expected type of the payload.
     * @return The subscription wrapper (never null).
     */
    private SubscriptionWrapper getOrCreateSubscription(String criteria, PayloadType payloadType) {
        String paramIndex = payloadType + "|" + criteria;
        SubscriptionWrapper wrapper = subscriptionsByParams.get(paramIndex);

        if (wrapper == null) {
            Subscription subscription = new Subscription();
            wrapper = new SubscriptionWrapper(paramIndex);
            SubscriptionChannelComponent channel = new SubscriptionChannelComponent();
            channel.setType(SubscriptionChannelType.RESTHOOK);
            channel.setEndpoint(callbackUrl + wrapper.getSubscriptionId());
            channel.setPayload(payloadType.mimeType);
            subscription.setCriteria(criteria);
            subscription.setReason("Fujion Subscriber");
            subscription.setChannel(channel);
            subscription.setStatus(SubscriptionStatus.REQUESTED);
            subscription.getMeta().setTag(Collections.singletonList(subscriptionTag));
            subscription = (Subscription) client.create().resource(subscription).prefer(PreferReturnEnum.REPRESENTATION)
                    .execute().getResource();
            wrapper.setSubscription(subscription);
            subscriptionsByParams.put(paramIndex, wrapper);
            subscriptionsById.put(wrapper.getSubscriptionId(), wrapper);
        }

        wrapper.incRefCount();
        return wrapper;
    }

    /**
     * Revokes a FHIR subscription.
     *
     * @param wrapper The subscription wrapper.
     */
    private void deleteSubscription(SubscriptionWrapper wrapper) {
        subscriptionsByParams.remove(wrapper.getParamIndex());
        subscriptionsById.remove(wrapper.getSubscriptionId());
        client.delete().resource(wrapper.getSubscription()).execute();
    }
    
}
