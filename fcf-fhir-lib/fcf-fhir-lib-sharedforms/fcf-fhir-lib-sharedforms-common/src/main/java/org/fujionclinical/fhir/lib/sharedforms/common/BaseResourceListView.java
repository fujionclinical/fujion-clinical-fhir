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
package org.fujionclinical.fhir.lib.sharedforms.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.BaseComponent;
import org.fujion.component.Html;
import org.fujion.component.Row;
import org.fujion.component.Window;
import org.fujion.page.PageUtil;
import org.fujion.thread.ICancellable;
import org.fujion.thread.ThreadedTask;
import org.fujionclinical.api.event.IEventSubscriber;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.patient.PatientContext;
import org.fujionclinical.fhir.api.common.core.AbstractFhirService;
import org.fujionclinical.fhir.api.common.core.NarrativeService;
import org.fujionclinical.fhir.subscription.common.BaseSubscriptionWrapper;
import org.fujionclinical.fhir.subscription.common.ISubscriptionCallback;
import org.fujionclinical.fhir.subscription.common.ResourceSubscriptionManager;
import org.fujionclinical.sharedforms.controller.ListFormController;
import org.fujionclinical.shell.elements.ElementPlugin;
import org.fujionclinical.ui.dialog.DialogUtil;
import org.fujionclinical.ui.util.FCFUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.hl7.fhir.instance.model.api.INarrative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for displaying FHIR resources in a columnar format.
 *
 * @param <R> Type of resource object.
 * @param <M> Type of model object.
 */
public abstract class BaseResourceListView<S extends AbstractFhirService, B extends IBaseBundle, R extends IBaseResource, M> extends ListFormController<M> {

    private static final Log log = LogFactory.getLog(BaseResourceListView.class);

    private static final String DETAIL_POPUP = FCFUtil.getResourcePath(BaseResourceListView.class) + "resourceListDetailPopup.fsp";

    private final List<BaseSubscriptionWrapper> subscriptions = new ArrayList<>();

    private final ISubscriptionCallback subscriptionListener = (eventName, resource) -> refresh();

    @WiredComponent
    protected Html detailView;

    protected IPatient patient;

    protected Class<R> resourceClass;

    private String detailTitle;

    private S fhirService;

    private NarrativeService narrativeService;

    private ResourceSubscriptionManager subscriptionManager;

    private final IEventSubscriber<IPatient> patientChangeListener = (eventName, patient) -> setPatient(patient);

    private String resourcePath;

    private Class<B> bundleClass;

    protected abstract void setup();

    protected void setup(
            Class<R> resourceClass,
            Class<B> bundleClass,
            String title,
            String detailTitle,
            String resourcePath,
            int sortBy,
            String... headers) {
        this.detailTitle = detailTitle;
        this.resourcePath = resourcePath;
        this.resourceClass = resourceClass;
        this.bundleClass = bundleClass;
        super.setup(title, sortBy, headers);
    }

    protected void createSubscriptions(Class<? extends IBaseResource> clazz) {
        createSubscription(clazz);
    }

    protected void createSubscription(Class<? extends IBaseResource> clazz) {
        String resourceName = clazz.getSimpleName();
        String id = patient.getId();
        String criteria = resourceName + "?subject=" + id;
        BaseSubscriptionWrapper wrapper = subscriptionManager.subscribe(criteria, subscriptionListener);

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

    /**
     * Override load list to clear display if no patient in context.
     */
    @Override
    protected void loadData() {
        if (patient == null) {
            asyncAbort();
            reset();
            status("No patient selected.");
        } else {
            super.loadData();
        }

        detailView.setContent(null);
    }

    @Override
    protected void requestData() {
        final String url = resourcePath.replace("#", patient.getId());

        startBackgroundThread(map -> {
            B bundle = fhirService.getClient().search().byUrl(url).returnBundle(bundleClass).execute();
            map.put("bundle", bundle);
        });
    }

    @Override
    protected void threadFinished(ICancellable thread) {
        ThreadedTask task = (ThreadedTask) thread;

        try {
            task.rethrow();
        } catch (Throwable e) {
            status("An unexpected error was encountered:  " + FCFUtil.formatExceptionForDisplay(e));
            return;
        }

        model.clear();
        initModel(processBundle((B) task.getAttribute("bundle")));
        renderData();
    }

    /**
     * Extracts results from the returned bundle. Override for special processing.
     *
     * @param bundle The bundle.
     * @return List of extracted resources.
     */
    protected abstract List<R> processBundle(B bundle);

    protected abstract void initModel(List<R> entries);

    @Override
    protected void asyncAbort() {
        abortBackgroundThreads();
    }

    /**
     * Show detail for specified component.
     *
     * @param item The component containing the model object.
     */
    protected void showDetail(BaseComponent item) {
        @SuppressWarnings("unchecked")
        M modelObject = item == null ? null : (M) item.getData();
        String detail = modelObject == null ? null : getDetail(modelObject);

        if (!StringUtils.isEmpty(detail)) {
            if (getShowDetailPane()) {
                detailView.setContent(detail);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("title", detailTitle);
                map.put("content", detail);
                map.put("allowPrint", getAllowPrint());
                try {
                    Window window = (Window) PageUtil
                            .createPage(DETAIL_POPUP, null, map).get(0);
                    window.modal(null);
                } catch (Exception e) {
                    DialogUtil.showError(e);
                }
            }
        }
    }

    protected String getDetail(M modelObject) {
        try {
            if (modelObject instanceof IDomainResource) {
                INarrative narrative = narrativeService.extractNarrative((IDomainResource) modelObject, true);
                return narrative == null ? null : narrative.getDivAsString();
            }
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * Display detail when row is selected.
     */
    @Override
    protected void rowSelected(Row row) {
        showDetail(row);
    }

    @Override
    public void onLoad(ElementPlugin plugin) {
        super.onLoad(plugin);
        setup();
        PatientContext.getPatientContext().addListener(patientChangeListener);
        setPatient(PatientContext.getActivePatient());
    }

    @Override
    public void onUnload() {
        PatientContext.getPatientContext().removeListener(patientChangeListener);
    }

    private void setPatient(IPatient patient) {
        this.patient = patient;

        try {
            removeSubscriptions();

            if (patient != null) {
                createSubscriptions(resourceClass);
            }
        } finally {
            refresh();
        }
    }

    public S getFhirService() {
        return fhirService;
    }

    public void setFhirService(S fhirService) {
        this.fhirService = fhirService;
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
