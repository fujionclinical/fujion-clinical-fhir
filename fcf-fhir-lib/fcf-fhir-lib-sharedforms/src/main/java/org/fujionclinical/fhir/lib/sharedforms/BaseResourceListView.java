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
package org.fujionclinical.fhir.lib.sharedforms;

import edu.utah.kmm.model.cool.foundation.entity.Person;
import edu.utah.kmm.model.cool.mediator.fhir.common.AbstractFhirDataSource;
import edu.utah.kmm.model.cool.util.CoolUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.fhir.api.common.core.NarrativeService;
import org.fujionclinical.fhir.subscription.common.BaseSubscriptionWrapper;
import org.fujionclinical.fhir.subscription.common.ISubscriptionCallback;
import org.fujionclinical.fhir.subscription.common.ResourceSubscriptionManager;
import org.fujionclinical.sharedforms.controller.AbstractResourceListView;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.hl7.fhir.instance.model.api.INarrative;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for displaying FHIR resources in a columnar format.
 *
 * @param <R> Type of resource object.
 * @param <M> Type of model object.
 */
@SuppressWarnings("rawtypes")
public abstract class BaseResourceListView<R extends IBaseResource, M, S extends AbstractFhirDataSource> extends AbstractResourceListView<R, M, S> {

    private static final Log log = LogFactory.getLog(BaseResourceListView.class);

    private final List<BaseSubscriptionWrapper> subscriptions = new ArrayList<>();

    private final ISubscriptionCallback subscriptionListener = (eventName, resource) -> refresh();

    private NarrativeService narrativeService;

    private ResourceSubscriptionManager subscriptionManager;

    protected void createSubscriptions(Class<? extends IBaseResource> clazz) {
        createSubscription(clazz);
    }

    protected void createSubscription(Class<? extends IBaseResource> clazz) {
        String resourceName = clazz.getSimpleName();
        String id = CoolUtils.getId(getPatient());
        String criteria = resourceName + "?subject=" + id;
        BaseSubscriptionWrapper wrapper = subscriptionManager.subscribe(criteria, subscriptionListener, getDataSource());

        if (wrapper != null) {
            subscriptions.add(wrapper);
        }
    }

    protected void removeSubscriptions() {
        for (BaseSubscriptionWrapper wrapper : subscriptions) {
            try {
                subscriptionManager.unsubscribe(wrapper, subscriptionListener);
                subscriptions.remove(wrapper);
            } catch (Exception e) {
                log.error("Error removing resource subscription", e);
            }
        }

        subscriptions.clear();
    }

    @Override
    protected void requestData() {
        startBackgroundThread(map -> map.put("results", getDataSource().searchResources(getResourceClass(), getQueryString())));
    }

    protected String getDetail(M modelObject) {
        try {
            if (modelObject instanceof IDomainResource) {
                INarrative narrative = narrativeService
                        .extractNarrative(getDataSource().getClient().getFhirContext(), (IDomainResource) modelObject, true);
                return narrative == null ? null : narrative.getDivAsString();
            }
        } catch (Exception e) {
            // NOP
        }

        return null;
    }

    @Override
    protected void afterPatientChange(Person patient) {
        removeSubscriptions();

        if (patient != null) {
            createSubscriptions(getResourceClass());
        }
    }

    public NarrativeService getNarrativeService() {
        return narrativeService;
    }

    public void setNarrativeService(NarrativeService narrativeService) {
        this.narrativeService = narrativeService;
    }

    public ResourceSubscriptionManager getResourceSubscriptionManager() {
        return subscriptionManager;
    }

    public void setResourceSubscriptionManager(ResourceSubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
    }

}
