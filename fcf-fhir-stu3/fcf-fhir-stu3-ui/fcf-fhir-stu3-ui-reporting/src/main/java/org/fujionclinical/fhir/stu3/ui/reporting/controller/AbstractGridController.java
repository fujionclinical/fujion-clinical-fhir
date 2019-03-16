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
import org.fujion.component.Checkbox;
import org.fujion.component.Grid;
import org.fujion.component.Row;
import org.fujion.component.Rows;
import org.fujionclinical.api.query.IQueryService;
import org.fujionclinical.fhir.stu3.ui.reporting.common.ReportConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a stateful controller that supports grid-based displays.
 *
 * @param <T> Query result class
 * @param <M> Model result class
 */
public abstract class AbstractGridController<T, M> extends AbstractBaseController<T, M> {
    
    private static final Log log = LogFactory.getLog(AbstractGridController.class);
    
    @WiredComponent(onFailure = OnFailure.IGNORE)
    private Checkbox chkExpandAll;
    
    @WiredComponent
    protected Grid grid;
    
    public AbstractGridController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet, String reportHeader, SupplementalQueryParam<?> ...params) {
        super(service, labelPrefix, propertyPrefix, printStyleSheet, reportHeader, params);
    }
    
    /**
     * Initializes the controller. Loads user preferences and properties.
     */
    @Override
    protected void initializeController() {
        if (grid.getRows() == null) {
            grid.addChild(new Rows());
        }
        
        setComponents(grid, grid.getRows());
        super.initializeController();
        
        boolean expandAll = getPropertyValue(ReportConstants.PROPERTY_ID_EXPAND_DETAIL, Boolean.class,
            chkExpandAll != null && chkExpandAll.isChecked());
        
        if (this.chkExpandAll != null) {
            this.chkExpandAll.setChecked(expandAll);
        }
        
    }
    
    /**
     * Clear selected items
     */
    @EventHandler(value = "click", target = "btnClear", onFailure = OnFailure.IGNORE)
    private void onClick$btnClear() {
        clearSelection();
    }
    
    /**
     * Clear the current selection, if any.
     */
    protected void clearSelection() {
        grid.getRows().clearSelected();
    }

    /**
     * The event handler for checkbox events. Expand/close all the nodes in pharmacy orders when
     * check/uncheck checkbox.
     */
    @EventHandler(value = "change", target = "@chkExpandAll", onFailure = OnFailure.IGNORE)
    private void onChange$chkExpandAll() {
        if (log.isTraceEnabled()) {
            log.trace("onCheck : expand detail event fired");
        }
        
        boolean expandAll = this.chkExpandAll.isChecked();
        
        if (log.isDebugEnabled()) {
            log.debug("Expand Detail: " + expandAll);
        }
        
        //AbstractRowRenderer.setExpandDetail(grid, expandAll);
    }
    
    /**
     * Returns a list of rows.
     *
     * @param selectedOnly If true, only selected rows are returned.
     * @return List of rows.
     */
    protected Iterable<Row> getRows(boolean selectedOnly) {
        Rows rows = grid.getRows();
        return selectedOnly ? rows.getSelected() : rows.getChildren(Row.class);
    }
    
    /**
     * Returns a list of model objects.
     *
     * @param selectedOnly If true, only selected objects are returned.
     * @return List of model objects.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected List<M> getObjects(boolean selectedOnly) {
        Iterable<Row> rows = getRows(selectedOnly);
        List<M> objects = new ArrayList<>();
        
        for (Row row : rows) {
            objects.add((M) row.getData());
        }
        
        return objects;
    }
    
    protected Grid getGrid() {
        return grid;
    }
}
