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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.annotation.EventHandler;
import org.fujion.annotation.OnFailure;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.DateRange;
import org.fujion.component.BaseComponent;
import org.fujion.component.BaseUIComponent;
import org.fujion.component.Combobox;
import org.fujion.component.Comboitem;
import org.fujion.model.*;
import org.fujionclinical.api.property.PropertyUtil;
import org.fujionclinical.api.query.DateQueryFilter;
import org.fujionclinical.api.query.DateQueryFilter.DateType;
import org.fujionclinical.api.query.DateQueryFilter.IDateTypeExtractor;
import org.fujionclinical.api.query.IQueryService;
import org.fujionclinical.fhir.stu3.ui.reporting.common.ReportConstants;
import org.fujionclinical.fhir.stu3.ui.reporting.common.ReportUtil;
import org.fujionclinical.ui.dialog.DateRangePicker;
import org.fujionclinical.ui.util.FCFUtil;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * This is a stateful controller that supports plugins that use a list or grid model and background
 * thread for data retrieval.
 *
 * @param <T> Query result class
 * @param <M> Model result class
 */
public abstract class AbstractBaseController<T, M> extends AbstractServiceController<T, M> implements IDateTypeExtractor<M> {

    private static final Log log = LogFactory.getLog(AbstractBaseController.class);

    // These components are auto-wired by the controller.

    @WiredComponent(onFailure = OnFailure.IGNORE)
    private DateRangePicker dateRangePicker;

    @WiredComponent(onFailure = OnFailure.IGNORE)
    private Combobox dateTypePicker;

    @WiredComponent(onFailure = OnFailure.IGNORE)
    private BaseComponent printRoot;

    // --- End of auto-wired section

    private BaseUIComponent baseComponent;

    private ISupportsModel<?> modelComponent;

    private DateQueryFilter<M> dateFilter;

    private final ListModel<M> listModel = new ListModel<>();

    private IComponentRenderer<?, M> renderer;

    private final String propertyPrefix;

    private final String printStyleSheet;

    private final String reportHeader;

    /**
     * Create the controller.
     *
     * @param service The is the data query service.
     * @param labelPrefix Prefix used to resolve label id's with placeholders.
     * @param propertyPrefix Prefix for property names.
     * @param printStyleSheet Optional style sheet to apply when printing.
     * @param params Optional supplemental query parameters.
     */
    public AbstractBaseController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
                                  String printStyleSheet, String reportHeader, SupplementalQueryParam<?> ...params) {
        super(service, labelPrefix, params);
        this.propertyPrefix = propertyPrefix;

        if (printStyleSheet != null && !printStyleSheet.startsWith("web/")) {
            printStyleSheet = FCFUtil.getResourcePath(getClass()) + printStyleSheet;
        }

        this.printStyleSheet = printStyleSheet;
        this.reportHeader = reportHeader == null ? "user" : reportHeader;
    }

    @SuppressWarnings("unchecked")
    protected IModelAndView<?, M> getModelAndView() {
        return (IModelAndView<?, M>) modelComponent.getModelAndView(Object.class);
    }

    /**
     * Re-renders a model object.
     *
     * @param object Model object
     */
    protected void rerender(M object) {
        getModelAndView().rerender(object);
    }

    /**
     * Sets the list model to use.
     *
     * @param model The list model.
     */
    protected void setModel(IListModel<M> model) {
        getModelAndView().setModel(model);
    }

    /**
     * Returns the date for the given result for filtering purposes.
     *
     * @param result Result from which to extract a date.
     * @param dateType The date type.
     * @return The extracted date.
     */
    @Override
    public abstract Date getDateByType(M result, DateType dateType);

    /**
     * Sets the component that displays the selectable elements.
     *
     * @param baseComponent The component (grid or listbox).
     * @param modelComponent The component to which rendered items will be added (rows or listbox).
     */
    /*package*/void setComponents(BaseUIComponent baseComponent, ISupportsModel<?> modelComponent) {
        this.baseComponent = baseComponent;
        this.modelComponent = modelComponent;
        setHideOnShowMessage(baseComponent);

        if (renderer != null) {
            setRenderer(renderer);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setRenderer(IComponentRenderer<?, M> renderer) {
        this.renderer = renderer;

        if (modelComponent != null) {
            modelComponent.getModelAndView().setRenderer((IComponentRenderer) renderer);
        }
    }

    /**
     * The list model has been updated. If the model is empty or null, displays the no data message.
     * This ultimately calls the abstract setModel method to allow the subclass to handle the
     * updated model.
     *
     * @param model The hybrid model.
     */
    private void updateModel(IListModel<M> model) {
        if (model == null || model.isEmpty()) {
            String msg = getLabel(ReportConstants.LABEL_ID_NO_DATA);
            log.trace(msg);
            showMessage(msg);
        } else {
            showMessage(null);
        }

        setModel(model);
        afterModelChanged();
    }

    /**
     * Override to perform any special operations after the model has been changed.
     */
    protected void afterModelChanged() {
    }

    /**
     * Returns the current list model.
     *
     * @return The list model.
     */
    protected ListModel<M> getListModel() {
        return listModel;
    }

    protected abstract List<M> getObjects(boolean selectedOnly);

    /**
     * Retrieves a property value of the specified data type. It examines the property value for a
     * type-compatible value. Failing that, it returns the specified default value.
     *
     * @param <V> Property value data type.
     * @param propName Name of property from which to retrieve the value.
     * @param clazz Expected data type of the property value.
     * @param dflt Default value to use if a suitable one cannot be found.
     * @return The value.
     */
    @SuppressWarnings("unchecked")
    protected <V> V getPropertyValue(String propName, Class<V> clazz, V dflt) {
        V value = null;

        if (propName != null) {
            propName = propName.replace("%", propertyPrefix == null ? "" : propertyPrefix);
            String val = StringUtils.trimToNull(PropertyUtil.getValue(propName));

            if (log.isDebugEnabled()) {
                log.debug("Property " + propName + " value: " + val);
            }

            if (clazz == String.class) {
                value = (V) (val);
            } else {
                Method method = BeanUtils.findMethod(clazz, "valueOf", String.class);

                if (method != null && method.getReturnType() == clazz) {
                    value = (V) parseString(method, val, null);
                }
            }
        }

        return value == null ? dflt : value;
    }

    /**
     * Uses the valueOf method in the target type class to convert one of two candidate values to
     * the target type. Failing that, it returns null.
     *
     * @param method The valueOf method in the target class.
     * @param value1 The first candidate value to try.
     * @param value2 The second candidate value to try.
     * @return The converted value if successful; null if not.
     */
    private Object parseString(Method method, String value1, String value2) {
        try {
            return method.invoke(null, value1);
        } catch (Exception e) {
            return value2 == null ? null : parseString(method, value2, null);
        }
    }

    /**
     * Initializes Controller.
     */
    @Override
    protected void initializeController() {
        super.initializeController();

        if (dateRangePicker != null) {
            String deflt = getPropertyValue(ReportConstants.PROPERTY_ID_DATE_RANGE, String.class, "Last Two Years");
            dateRangePicker.setSelectedItem(dateRangePicker.findMatchingItem(deflt));
            initDateFilter().setDateRange(dateRangePicker.getSelectedRange());
        }

        if (dateTypePicker != null) {
            for (DateType dt : DateType.values()) {
                String lbl = getLabel(ReportConstants.LABEL_ID_SORT_MODE.replace("$", dt.name().toLowerCase()));
                Comboitem item = new Comboitem(lbl);
                item.setData(dt);
                dateTypePicker.addChild(item);
            }
            DateType sortModePref = getPropertyValue(ReportConstants.PROPERTY_ID_SORT_MODE, DateType.class, DateType.MEASURED);
            Comboitem item = (Comboitem) dateTypePicker.findChildByData(sortModePref);
            dateTypePicker.setSelectedItem(item == null ? (Comboitem) dateTypePicker.getFirstChild() : item);
            dateTypePicker.setReadonly(true);
            initDateFilter().setDateType(sortModePref);
        }

        if (dateFilter != null) {
            registerQueryFilter(dateFilter);
        }

    }

    private DateQueryFilter<M> initDateFilter() {
        if (dateFilter == null) {
            dateFilter = new DateQueryFilter<>(this);
        }

        return dateFilter;
    }

    /**
     * Submits a data fetch request in the background.
     */
    @Override
    protected void fetchData() {
        listModel.clear();
        super.fetchData();
    }

    /**
     * Event handler to handle changes in the DateType of a query
     */
    @EventHandler(value = "change", target = "@dateTypePicker", onFailure = OnFailure.IGNORE)
    private void onChange$dateTypePicker() {
        DateType dateType = getDateType();

        log.trace("Handling onSelect of dateTypePicker Combobox");

        if (log.isDebugEnabled()) {
            log.debug("dateTypePicker value: " + dateType);
        }

        dateFilter.setDateType(dateType);
    }

    /**
     * The event handler for DatePicker events. Compares DatePicker range against cached date range.
     * If out of range, {@link #fetchData()} is called and cache is refreshed
     */
    @EventHandler(value = "selectRange", target = "@dateRangePicker", onFailure = OnFailure.IGNORE)
    private void onSelectRange$dateRangePicker() {
        DateRange dateRange = getDateRange();

        if (log.isTraceEnabled()) {
            log.trace("DatePicker range: " + dateRange);
        }

        dateFilter.setDateRange(dateRange);
    }

    @Override
    protected void modelChanged(List<M> filteredModel) {
        listModel.clear();

        if (filteredModel != null) {
            listModel.addAll(filteredModel);
        }

        updateModel(listModel);
    }

    /**
     * Re-renders the current model.
     */
    protected void rerender() {
        applyFilters();
    }

    /**
     * Returns date range from picker.
     *
     * @return The date range.
     */
    protected DateRange getDateRange() {
        return dateRangePicker == null ? null : dateRangePicker.getSelectedRange();
    }

    /**
     * Returns date type from picker.
     *
     * @return The date type.
     */
    protected DateType getDateType() {
        Comboitem item = dateTypePicker == null ? null : dateTypePicker.getSelectedItem();
        return item == null ? dateFilter.getDateType() : (DateType) item.getData();
    }

    /**
     * Invoke refresh upon refresh button click.
     */
    @EventHandler(value = "click", target = "btnRefresh", onFailure = OnFailure.IGNORE)
    private void onClick$btnRefresh() {
        refresh();
    }

    protected void print(BaseComponent root) {
        String printTitle = getLabel(ReportConstants.LABEL_ID_TITLE);
        ReportUtil.print((BaseUIComponent) (root == null ? baseComponent.getParent() : root), printTitle,
            reportHeader, printStyleSheet);
    }

    @EventHandler(value = "click", target = "btnPrint", onFailure = OnFailure.IGNORE)
    private void onClick$btnPrint() {
        print(printRoot);
    }

}
