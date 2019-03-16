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
package org.fujionclinical.fhir.stu3.ui.reporting.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.annotation.EventHandler;
import org.fujion.annotation.OnFailure;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.StrUtil;
import org.fujion.component.BaseComponent;
import org.fujion.component.BaseUIComponent;
import org.fujion.component.Label;
import org.fujion.component.Style;
import org.fujion.event.Event;
import org.fujion.event.EventUtil;
import org.fujionclinical.api.query.*;
import org.fujionclinical.api.thread.IAbortable;
import org.fujionclinical.fhir.stu3.ui.reporting.common.ReportConstants;
import org.fujionclinical.shell.elements.ElementPlugin;
import org.fujionclinical.shell.plugins.PluginController;
import org.fujionclinical.ui.util.FCFUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * This is a stateful controller that supports plugins that perform background data retrieval using
 * an IQueryService-compliant service.
 *
 * @param <T> Query result class
 * @param <M> Model result class
 */
public abstract class AbstractServiceController<T, M> extends PluginController {
    
    private static final Log log = LogFactory.getLog(AbstractServiceController.class);

    /**
     * A supplemental query parameter that will be added to the query prior to execution.  This is used
     * for parameters that may change their value due to external events, requiring a refresh of the display.
     *
     * @param <P> The datatype of the parameter value.
     */
    protected abstract static class SupplementalQueryParam<P> {

        private final String paramName;

        private P paramValue;

        private Consumer<SupplementalQueryParam<?>> callback;

        protected SupplementalQueryParam(String paramName, P initialValue) {
            this.paramName = paramName;
            this.paramValue = initialValue;
        }

        /**
         * Returns the current parameter value.
         *
         * @return The current parameter value.
         */
        protected P getValue() {
            return paramValue;
        }

        /**
         * Sets the parameter value, triggering a value changed event.
         *
         * @param value The new parameter value.
         */
        protected void setValue(P value) {
            paramValue = value;
            valueChanged();
        }

        /**
         * Destroys the object.  The default implementation just sets the parameter's value to null.
         */
        protected void destroy() {
            paramValue = null;
        }

        /**
         * Returns the id of a label value to be displayed in the event that the parameter lacks a required value.
         *
         * @return Label id if a required value is not present; otherwise null.
         */
        protected abstract String hasRequired();

        /**
         * Initialize the query context with this parameter's value.
         *
         * @param context The query context to initialize.
         */
        protected void initContext(IQueryContext context) {
            context.setParam(paramName, paramValue);
        }

        /**
         * Called when the parameter's value has changed, notifying the subscriber via the callback.
         */
        private void valueChanged() {
            if (callback != null) {
                callback.accept(this);
            }
        }

        /**
         * Sets the callback to be invoked when the parameter value changes.
         *
         * @param callback The callback.
         */
        private void setCallback(Consumer<SupplementalQueryParam<?>> callback) {
            this.callback = callback;
        }

    }

    /**
     * Manages all registered supplemental query parameters.
     */
    private class SupplementalQueryParams {

        private final List<SupplementalQueryParam<?>> params = new ArrayList<>();

        void register(SupplementalQueryParam<?> ...params) {
            for (SupplementalQueryParam<?> param: params) {
                this.params.add(param);
                param.setCallback(prm -> AbstractServiceController.this.onParameterChanged(param));
            }
        }

        String hasRequired() {
            for (SupplementalQueryParam<?> param: params) {
                String labelId = param.hasRequired();

                if (labelId != null) {
                    return labelId;
                }
            }

            return null;
        }

        void initContext(IQueryContext context) {
            params.forEach(param -> param.initContext(context));
        }

        void destroy() {
            params.forEach(param -> param.destroy());
            params.clear();
        }
    }

    protected class QueryFinishedEvent extends Event {
        
        private final IAbortable thread;
        
        public QueryFinishedEvent(IAbortable thread, IQueryResult<T> result, BaseComponent target) {
            super("queryFinished", target, result);
            this.thread = thread;
        }
        
        public IAbortable getThread() {
            return thread;
        }
        
        @SuppressWarnings("unchecked")
        public IQueryResult<T> getResult() {
            return (IQueryResult<T>) getData();
        }
    }
    
    /**
     * Listener for query completion callbacks. Ensures that result callbacks occur in the main
     * event thread.
     */
    private final IQueryCallback<T> queryListener = new IQueryCallback<T>() {
        
        @Override
        public void onQueryFinish(IAbortable thread, IQueryResult<T> result) {
            EventUtil.post(new QueryFinishedEvent(thread, result, root));
        }
        
        @Override
        public void onQueryStart(IAbortable thread) {
            addThread(thread);
        }
    };
    
    /**
     * Listener for query filter changes.
     */
    private final IQueryFilterChanged<M> queryFilterChangedListener = new IQueryFilterChanged<M>() {
        
        @Override
        public void onFilterChanged(IQueryFilter<M> filter) {
            if (filter.updateContext(queryContext)) {
                refresh(); // hit database
            } else {
                applyFilters();
            }
        }
    };
    
    @WiredComponent(onFailure = OnFailure.IGNORE)
    /*package*/Label lblMessage;
    
    private boolean backgroundFetch = false;
    
    private boolean deferredFetch = true;
    
    private final IQueryContext queryContext = new QueryContext();
    
    private final QueryFilterSet<M> queryFilters = new QueryFilterSet<>();

    private final SupplementalQueryParams supplementalQueryParams = new SupplementalQueryParams();

    private final IQueryService<T> service;
    
    private final String labelPrefix;
    
    private List<M> model;
    
    private List<M> filteredModel;
    
    private boolean fetchPending;
    
    private BaseUIComponent hideOnShowMessage;
    
    /**
     * Create the controller.
     *
     * @param service The is the data query service.
     * @param labelPrefix Prefix used to resolve label id's with placeholders.
     */
    public AbstractServiceController(IQueryService<T> service, String labelPrefix, SupplementalQueryParam<?> ...params) {
        super();
        this.service = service;
        this.labelPrefix = labelPrefix;
        queryFilters.addListener(queryFilterChangedListener);
        supplementalQueryParams.register(params);
    }
    
    // Begin override section.  The following methods will likely require implementation/overrides to produce desired behaviors.
    
    /**
     * Override to implement any special controller initialization logic.
     */
    protected void initializeController() {
        log.trace("Initializing Controller");
        
        if (lblMessage != null) {
            lblMessage.addStyle("text-align", "center");
        }

        Style style = new Style();
        style.setSrc(FCFUtil.getResourcePath(AbstractServiceController.class, 1) + "common.css");
        root.getPage().addChild(style);
    }
    
    /**
     * Override to respond to a model change.
     *
     * @param filteredModel The filtered model.
     */
    protected void modelChanged(List<M> filteredModel) {
    }
    
    /**
     * Override to add additional parameters to query context.
     *
     * @param context The query context.
     */
    protected void prepareQueryContext(IQueryContext context) {
    }
    
    // End override section.
    
    protected void setModel(List<M> model) {
        this.model = model == null ? Collections.<M> emptyList() : model;
        applyFilters();
    }
    
    protected List<M> getModel() {
        return model;
    }
    
    protected List<M> getFilteredModel() {
        return filteredModel;
    }
    
    protected void applyFilters() {
        filteredModel = queryFilters.filter(model);
        modelChanged(filteredModel);
    }
    
    /**
     * Registers a query filter.
     *
     * @param queryFilter A query filter.
     */
    public void registerQueryFilter(IQueryFilter<M> queryFilter) {
        queryFilters.add(queryFilter);
    }
    
    /**
     * Unregisters a query filter.
     *
     * @param queryFilter The query filter.
     */
    public void unregisterQueryFilter(IQueryFilter<M> queryFilter) {
        queryFilters.remove(queryFilter);
    }
    
    /**
     * If there is a deferred fetch operation when the plugin is activated, invoke it now.
     */
    @Override
    public void onActivate() {
        super.onActivate();
        
        if (this.fetchPending) {
            log.trace("Processing deferred data request.");
            fetchData();
        }
    }
    
    /**
     * Clean up.
     */
    @Override
    public void onUnload() {
        super.onUnload();
        supplementalQueryParams.destroy();
    }
    
    /**
     * Returns true if data fetches will be deferred until the plugin is active.
     *
     * @return The deferred fetch setting.
     */
    public boolean isDeferredFetch() {
        return deferredFetch;
    }
    
    /**
     * Setting to true will defer data fetches until the plugin is active.
     *
     * @param deferredFetch The deferred fetch setting.
     */
    public void setDeferredFetch(boolean deferredFetch) {
        this.deferredFetch = deferredFetch;
    }
    
    /**
     * Returns true if data fetches are to be asynchronous.
     *
     * @return The background fetch setting.
     */
    public boolean isBackgroundFetch() {
        return backgroundFetch;
    }
    
    /**
     * Setting to true will cause data fetches to occur asynchronously.
     *
     * @param backgroundFetch The background fetch setting.
     */
    public void setBackgroundFetch(boolean backgroundFetch) {
        this.backgroundFetch = backgroundFetch;
    }
    
    /**
     * Returns a label value given its id. Will attempt to find a label for the current label
     * prefix. Failing that, will use the default label prefix.
     *
     * @param labelId Id of the label sought.
     * @return The label value, or the default value if none found.
     */
    protected String getLabel(String labelId) {
        String label = getLabel(labelId, labelPrefix);
        return label != null ? label : getLabel(labelId, "reporting");
    }
    
    /**
     * Returns a label value given its id. Recognizes placeholders in label names, replacing them
     * with the default label prefix.
     *
     * @param labelId Id of the label sought.
     * @param placeholder Placeholder value to substitute.
     * @return The label value, or the default value if none found.
     */
    protected String getLabel(String labelId, String placeholder) {
        return placeholder == null ? null : StrUtil.getLabel(labelId.replace("%", placeholder));
    }
    
    /**
     * Evaluates the query context to determine if all required parameters are present.
     *
     * @return Null if all required parameters are present. Otherwise, the id of a label to display.
     */
    protected String hasRequired() {
        String labelId = supplementalQueryParams.hasRequired();
        return labelId != null ? labelId : service.hasRequired(queryContext) ? null : ReportConstants.LABEL_ID_MISSING_PARAMETER;
    }
    
    /**
     * Submits a data fetch request in the background.
     */
    protected void fetchData() {
        fetchPending = false;
        abortBackgroundThreads();
        showMessage("");
        
        if (service == null) {
            return;
        }
        
        showBusy(getLabel(ReportConstants.LABEL_ID_FETCHING));
        queryContext.reset();
        supplementalQueryParams.initContext(queryContext);
        queryFilters.updateContext(queryContext);
        prepareQueryContext(queryContext);
        String msg = hasRequired();
        
        if (msg == null) {
            if (backgroundFetch) {
                log.trace("Starting background data retrieval.");
                service.fetch(queryContext, queryListener);
            } else {
                EventUtil.post("fetchData", root, null);
            }
        } else {
            log.trace(msg);
            showMessage(getLabel(msg));
        }
    }
    
    /**
     * Foreground fetch of data. This is done as an echo event to ensure that the busy message is
     * cleared in the event of an exception.
     */
    @EventHandler("fetchData")
    private void onFetchData() {
        showBusy(null);
        queryFinished(service.fetch(queryContext));
    }
    
    /**
     * Event listener to ensure that query callbacks are delivered on the main thread.
     *
     * @param event Event containing the query result.
     */
    public void onQueryFinished(QueryFinishedEvent event) {
        removeThread(event.getThread());
        queryFinished(event.getResult());
    }
    
    /**
     * Called when a background query has completed.
     *
     * @param result The query result.
     */
    protected void queryFinished(IQueryResult<T> result) {
        log.trace("Finished background data retrieval.");
        model = null;
        
        switch (result.getStatus()) {
            case COMPLETED:
                model = toModel(result.getResults());
                applyFilters();
                break;
                
            case ABORTED:
                showMessage("@reporting.plugin.status.aborted");
                break;
                
            case ERROR:
                Throwable t = (Throwable) result.getMetadata("exception");
                log.error("Background thread threw an exception.", t);
                showMessage("@reporting.plugin.error.unexpected", true);
                break;
        }
    }
    
    /**
     * Implement to convert query results to model.
     *
     * @param results Query results.
     * @return Model objects corresponding to query results.
     */
    protected abstract List<M> toModel(List<T> results);
    
    /**
     * Displays a message to client.
     *
     * @param message Message to display to client. If null, message label is hidden.
     */
    public void showMessage(String message) {
        showMessage(message, false);
    }
    
    /**
     * Displays a message to client.
     *
     * @param message Message to display to client. If null, message label is hidden.
     * @param isError If true, highlight the message to indicate an error.
     */
    public void showMessage(String message, boolean isError) {
        showBusy(null);
        message = StrUtil.formatMessage(message);
        boolean show = message != null;
        
        if (lblMessage != null) {
            lblMessage.setVisible(show);
            lblMessage.setLabel(show ? message : "");
            lblMessage.toggleClass("alert-danger", "alert-warning", isError);
        }
        
        if (hideOnShowMessage != null) {
            hideOnShowMessage.setVisible(!show);
        }
    }
    
    /**
     * Overriding super class
     *
     * @see org.fujionclinical.ui.controller.FrameworkController#afterInitialized(BaseComponent)
     * @param comp Component
     */
    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        initializeController();
        refresh();
    }
    
    /**
     * Called if a supplemental parameter value has changed.
     *
     * @param param The parameter whose value has changed.
     */
    protected void onParameterChanged(SupplementalQueryParam<?> param) {
        refresh();
    }

    /**
     * Refreshes the data by re-fetching from the data source. If the plugin is not active, the
     * fetch request is deferred.
     */
    @Override
    public void refresh() {
        log.trace("Refreshing view.");
        abortBackgroundThreads();
        
        if (!deferredFetch || isActive()) {
            fetchData();
        } else {
            this.fetchPending = true;
        }
    }
    
    /**
     * Register fetch properties with container.
     */
    @Override
    public void onLoad(ElementPlugin plugin) {
        super.onLoad(plugin);
        plugin.registerProperties(this, "backgroundFetch", "deferredFetch");
    }
    
    /**
     * Returns the component to be hidden when a status message is displayed. May be null.
     *
     * @return The hide-on-show-message setting.
     */
    public BaseComponent getHideOnShowMessage() {
        return hideOnShowMessage;
    }
    
    /**
     * Sets the component to be hidden when a status message is displayed. May be null.
     *
     * @param hideOnShowMessage The hide-on-show-message setting.
     */
    public void setHideOnShowMessage(BaseUIComponent hideOnShowMessage) {
        this.hideOnShowMessage = hideOnShowMessage;
    }
    
}
