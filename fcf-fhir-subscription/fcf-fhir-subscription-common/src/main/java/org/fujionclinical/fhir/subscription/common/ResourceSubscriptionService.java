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
package org.fujionclinical.fhir.subscription.common;

import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.clinicalontology.terminology.impl.ConceptImpl;
import org.coolmodel.mediator.fhir.common.AbstractFhirDataSource;
import org.fujion.common.Assert;
import org.fujionclinical.api.event.EventMessage;
import org.fujionclinical.api.event.EventUtil;
import org.fujionclinical.api.messaging.Message;
import org.fujionclinical.api.messaging.ProducerService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing FHIR resource subscriptions. It provides a bridge between FHIR's
 * subscription framework and FCF's event framework. Each registered FHIR subscription is indexed by
 * a unique key and by its associated criteria. Each subscription is associated with a unique event
 * name. When the application receives a subscription notification from the FHIR server via one of
 * the supported mechanisms (preferably via a REST callback), this service may be invoked to deliver
 * the notification to each of the FCF subscribers by means of the associated event name. The
 * service also manages the lifecycle of the subscriptions, creating and revoking them as required.
 */
public class ResourceSubscriptionService implements BeanPostProcessor {

    public enum PayloadType {
        NONE(null), XML("application/fhir+xml"), JSON("application/fhir+json");

        private final String mimeType;

        PayloadType(String mimeType) {
            this.mimeType = mimeType;
        }

        @Override
        public String toString() {
            return mimeType;
        }
    }

    private static final TokenClientParam TAG = new TokenClientParam("tag");

    private final Log log = LogFactory.getLog(ResourceSubscriptionService.class);

    private final ProducerService producer;

    private final boolean disabled;

    private final String callbackUrl;

    private final Map<String, BaseSubscriptionFactory> factories = new HashMap<>();

    private final ConceptImpl subscriptionTag;

    private final Map<String, BaseSubscriptionWrapper<?>> subscriptionsByParams = new HashMap<>();

    private final Map<String, BaseSubscriptionWrapper<?>> subscriptionsById = new HashMap<>();

    /**
     * Create the resource subscription service.
     *
     * @param producer    The message producer for delivering events to subscribers.
     * @param callbackUrl The callback URL to be associated with new subscriptions. If no callback
     *                    URL is specified, this service will be disabled.
     */
    public ResourceSubscriptionService(
            ProducerService producer,
            String callbackUrl) {
        this.producer = producer;
        disabled = StringUtils.isEmpty(callbackUrl);
        this.callbackUrl = disabled ? null : callbackUrl.endsWith("/") ? callbackUrl : callbackUrl + "/";
        subscriptionTag = new ConceptImpl(callbackUrl, "ResourceSubscription", null);
        destroy();
        log.info("FHIR Resource Subscription Service is " + (disabled ? "disabled." : "enabled."));
    }

    /**
     * Delete any old subscriptions upon startup/shutdown.
     */
    public void destroy() {
        if (!disabled) {
            ICriterion<?> criterion = TAG.exactly().systemAndCode(subscriptionTag.getCodeSystemAsString(),
                    subscriptionTag.getCode());

            for (BaseSubscriptionFactory factory : factories.values()) {
                try {
                    factory.getDataSource().getClient().delete().resourceConditionalByType("Subscription").where(criterion).execute();
                } catch (Exception e) {
                    log.error("Error attempting to delete old subscription resources for data source " + factory.getDataSource().getId(), e);
                }
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
     * Associate a FCF event with a FHIR subscription (creating a new FHIR subscription as
     * necessary).
     *
     * @param criteria The subscription criteria (see FHIR specification).
     * @param dataSource The data source.
     * @return The subscription wrapper.
     */
    public synchronized BaseSubscriptionWrapper<?> subscribe(
            String criteria,
            AbstractFhirDataSource<?, ?> dataSource) {
        return subscribe(criteria, null, dataSource);
    }

    /**
     * Associate a FCF event with a FHIR subscription (creating a new FHIR subscription as
     * necessary).
     *
     * @param criteria    The subscription criteria (see FHIR specification).
     * @param payloadType The expected type of the payload.
     * @param dataSource The data source.
     * @return The subscription wrapper.
     */
    public synchronized BaseSubscriptionWrapper<?> subscribe(
            String criteria,
            PayloadType payloadType,
            AbstractFhirDataSource<?, ?> dataSource) {
        return disabled ? null : getOrCreateSubscription(criteria, payloadType == null ? PayloadType.NONE : payloadType, dataSource);
    }

    /**
     * Unsubscribe from a FHIR subscription (revoking the FHIR subscription if there are no further
     * references).
     *
     * @param wrapper The subscription wrapper.
     * @return The subscription wrapper.
     */
    public synchronized BaseSubscriptionWrapper<?> unsubscribe(BaseSubscriptionWrapper<?> wrapper) {
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
    public synchronized void unsubscribe(Collection<BaseSubscriptionWrapper<?>> wrappers) {
        if (wrappers != null) {
            for (BaseSubscriptionWrapper<?> wrapper : wrappers) {
                unsubscribe(wrapper);
            }
        }
    }

    /**
     * Notify all event subscribers of a subscription notification.
     *
     * @param id      Unique identifier of the FHIR subscription.
     * @param payload Serialized resource (may be null).
     * @return True if the subscription notification was delivered.
     */
    protected synchronized boolean notifySubscribers(
            String id,
            String payload) {
        BaseSubscriptionWrapper<?> wrapper = subscriptionsById.get(id);
        boolean found = wrapper != null;

        if (found) {
            IBaseResource resource = wrapper.parseResource(payload);
            String eventName = wrapper.getEventName();
            Message message = new EventMessage(eventName, resource);
            producer.publish(EventUtil.getChannelName(eventName), message);
        }

        return found;
    }

    /**
     * Returns a FHIR subscription wrapper from the list of active subscriptions, creating one if it
     * does not exist.
     *
     * @param criteria    The subscription criteria.
     * @param payloadType The expected type of the payload.
     * @param dataSource The data source.
     * @return The subscription wrapper (never null).
     */
    private BaseSubscriptionWrapper<?> getOrCreateSubscription(
            String criteria,
            PayloadType payloadType,
            AbstractFhirDataSource<?, ?> dataSource) {
        String paramIndex = payloadType + "|" + criteria;
        BaseSubscriptionWrapper<?> wrapper = subscriptionsByParams.get(paramIndex);

        if (wrapper == null) {
            BaseSubscriptionFactory factory = factories.get(dataSource.getId());
            Assert.notNull(factory, () -> "No subscription factory is registered to data source " + dataSource.getId());
            wrapper = factory.create(paramIndex, callbackUrl, payloadType, criteria, subscriptionTag);
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
    private void deleteSubscription(BaseSubscriptionWrapper<?> wrapper) {
        subscriptionsByParams.remove(wrapper.getParamIndex());
        subscriptionsById.remove(wrapper.getSubscriptionId());
        wrapper.delete();
    }

    @Override
    public Object postProcessAfterInitialization(
            Object bean,
            String beanName) throws BeansException {
        if (bean instanceof BaseSubscriptionFactory) {
            BaseSubscriptionFactory factory = (BaseSubscriptionFactory) bean;
            factories.put(factory.getDataSourceId(), factory);
        }

        return bean;
    }

}
