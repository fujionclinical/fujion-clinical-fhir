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

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.PreferReturnEnum;
import edu.utah.kmm.model.cool.mediator.fhir.core.AbstractFhirDataSource;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Wraps a FHIR subscription resource, adding necessary metadata for managing the subscription.
 */
public abstract class BaseSubscriptionWrapper<T extends IBaseResource> {

    public static final String EVENT_ROOT = "FHIR.SUB";

    private final String paramIndex;

    private final String subscriptionId;

    private final AbstractFhirDataSource dataSource;

    private T subscription;

    private boolean initialized;

    private int refCount;

    protected static String getParamIndexKey(
            String criteria,
            ResourceSubscriptionService.PayloadType payloadType) {
        return payloadType.name() + "|" + criteria;
    }

    /**
     * Create the subscription wrapper.
     *
     * @param subscription The subscription to wrap.
     * @param paramIndex   The index for looking up by criteria/payload type.
     */
    protected BaseSubscriptionWrapper(
            T subscription,
            String paramIndex,
            AbstractFhirDataSource dataSource) {
        this.subscription = subscription;
        this.paramIndex = paramIndex;
        this.dataSource = dataSource;
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
    protected abstract String getCriteria();

    /**
     * Return the name of the event used for callbacks.
     *
     * @return Event name used for callbacks.
     */
    public String getEventName() {
        return EVENT_ROOT + "." + subscriptionId;
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

    public AbstractFhirDataSource getDataSource() {
        return dataSource;
    }

    public BaseSubscriptionWrapper initialize() {
        if (!initialized) {
            initialized = true;
            subscription = (T) dataSource.getClient().create().resource(subscription).prefer(PreferReturnEnum.REPRESENTATION).execute().getResource();
        }

        return this;
    }

    /**
     * Parses a resource from the raw payload.
     *
     * @param payload Serialized form of the resource (may be null).
     * @return The parsed resource (may be null).
     */
    public IBaseResource parseResource(String payload) {
        IBaseResource resource = null;
        payload = StringUtils.trimToNull(payload);

        if (payload != null) {
            IParser parser = payload.startsWith("{") ? dataSource.getClient().getFhirContext().newJsonParser()
                    : dataSource.getClient().getFhirContext().newXmlParser();

            try {
                resource = parser.parseResource(payload);
            } catch (Exception e) {
                throw new RuntimeException("Unable to parse payload in subscription request", e);
            }
        }

        return resource;
    }

    public T getWrapped() {
        Assert.notNull(subscription, "Subscription has been deleted.");
        Assert.isTrue(initialized, "Subscription has not been initialized.");
        return subscription;
    }

    public void delete() {
        dataSource.getClient().delete().resource(subscription).execute();
        subscription = null;
    }

}
