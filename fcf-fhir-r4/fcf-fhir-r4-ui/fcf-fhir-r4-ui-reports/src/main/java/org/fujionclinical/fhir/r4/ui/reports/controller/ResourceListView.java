/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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
package org.fujionclinical.fhir.r4.ui.reports.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.BaseComponent;
import org.fujion.component.Html;
import org.fujion.component.Row;
import org.fujion.component.Window;
import org.fujion.page.PageUtil;
import org.fujionclinical.api.event.IGenericEvent;
import org.fujionclinical.fhir.common.ui.reports.ReportConstants;
import org.fujionclinical.fhir.r4.api.common.BaseService;
import org.fujionclinical.fhir.r4.api.common.FhirUtil;
import org.fujionclinical.fhir.r4.api.common.NarrativeService;
import org.fujionclinical.fhir.r4.api.patient.PatientContext;
import org.fujionclinical.fhir.r4.api.subscription.ISubscriptionCallback;
import org.fujionclinical.fhir.r4.api.subscription.ResourceSubscriptionManager;
import org.fujionclinical.fhir.r4.api.subscription.SubscriptionWrapper;
import org.fujionclinical.shell.elements.ElementPlugin;
import org.fujionclinical.ui.dialog.DialogUtil;
import org.fujionclinical.ui.sharedforms.ListFormController;
import org.fujionclinical.ui.thread.ThreadEx;
import org.fujionclinical.ui.thread.ThreadEx.IRunnable;
import org.fujionclinical.ui.util.FCFUtil;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.INarrative;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for cover sheet components.
 *
 * @param <R> Type of resource object.
 * @param <M> Type of model object.
 */
public abstract class ResourceListView<R extends IBaseResource, M> extends ListFormController<M> {
    
    private static final Log log = LogFactory.getLog(ResourceListView.class);
    
    @WiredComponent
    protected Html detailView;
    
    protected Patient patient;
    
    protected int asyncHandle;
    
    private String detailTitle;
    
    private BaseService fhirService;

    private NarrativeService narrativeService;

    private ResourceSubscriptionManager subscriptionManager;
    
    private final List<SubscriptionWrapper> subscriptions = new ArrayList<>();
    
    private String resourcePath;
    
    private Class<R> resourceClass;
    
    private final ISubscriptionCallback subscriptionListener = (eventName, resource) -> refresh();

    private final IGenericEvent<Patient> patientChangeListener = (eventName, patient) -> setPatient(patient);
    
    protected abstract void setup();
    
    protected void setup(Class<R> resourceClass, String title, String detailTitle, String resourcePath, int sortBy,
                         String... headers) {
        this.detailTitle = detailTitle;
        this.resourcePath = resourcePath;
        this.resourceClass = resourceClass;
        super.setup(title, sortBy, headers);
    }
    
    protected void createSubscriptions(Class<? extends IBaseResource> clazz) {
        createSubscription(clazz);
    }
    
    protected void createSubscription(Class<? extends IBaseResource> clazz) {
        String resourceName = clazz.getSimpleName();
        String id = patient.getIdElement().getIdPart();
        String criteria = resourceName + "?subject=" + id;
        SubscriptionWrapper wrapper = subscriptionManager.subscribe(criteria, subscriptionListener);

        if (wrapper != null) {
            subscriptions.add(wrapper);
        }
    }
    
    protected void removeSubscriptions() {
        for (SubscriptionWrapper wrapper : subscriptions) {
            try {
                subscriptionManager.unsubscribe(wrapper, subscriptionListener);
                subscriptions.remove(wrapper);
            } catch (Exception e) {
                log.error("Error removing resource subscription", e);
            }
        }

        subscriptions.clear();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Object transformData(Object data) {
        if (data instanceof IBaseDatatype) {
            return FhirUtil.getDisplayValueForType((IBaseDatatype) data);
        }
        
        if (data instanceof List) {
            List<?> c = (List<?>) data;
            
            if (c.isEmpty()) {
                return "";
            }

            if (c.get(0) instanceof IBaseDatatype) {
                return FhirUtil.getDisplayValueForTypes((List<IBaseDatatype>) c, ", ");
            }
        }
        
        return super.transformData(data);
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
        final String url = resourcePath.replace("#", patient.getIdElement().getIdPart());
        
        startBackgroundThread(new IRunnable() {
            
            @Override
            public void run(ThreadEx thread) {
                Bundle bundle = fhirService.getClient().search().byUrl(url).returnBundle(Bundle.class).execute();
                thread.setAttribute("bundle", bundle);
            }
            
            @Override
            public void abort() {
            }
            
        });
    }
    
    @Override
    protected void threadFinished(ThreadEx thread) {
        try {
            thread.rethrow();
        } catch (Throwable e) {
            status("An unexpected error was encountered:  " + FCFUtil.formatExceptionForDisplay(e));
            return;
        }
        
        model.clear();
        initModel(processBundle((Bundle) thread.getAttribute("bundle")));
        renderData();
    }
    
    /**
     * Extracts results from the returned bundle. Override for special processing.
     *
     * @param bundle The bundle.
     * @return List of extracted resources.
     */
    protected List<R> processBundle(Bundle bundle) {
        return FhirUtil.getEntries(bundle, resourceClass);
    }
    
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
                    Window window = (Window) PageUtil.createPage(ReportConstants.RESOURCE_PREFIX + "resourceListDetailPopup.fsp", null, map).get(0);
                    window.modal(null);
                } catch (Exception e) {
                    DialogUtil.showError(e);
                }
            }
        }
    }
    
    protected String getDetail(M modelObject) {
        try {
            if (modelObject instanceof IBaseResource) {
                INarrative narrative = narrativeService.extractNarrative((IBaseResource) modelObject, true);
                return narrative == null ? null : narrative.getDivAsString();
            }
        } catch (Exception e) {}

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
    
    private void setPatient(Patient patient) {
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

    public BaseService getFhirService() {
        return fhirService;
    }
    
    public void setFhirService(BaseService fhirService) {
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
