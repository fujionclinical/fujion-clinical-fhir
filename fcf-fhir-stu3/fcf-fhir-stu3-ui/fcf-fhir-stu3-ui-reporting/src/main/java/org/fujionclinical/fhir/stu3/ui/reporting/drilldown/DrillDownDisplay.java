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
package org.fujionclinical.fhir.stu3.ui.reporting.drilldown;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.annotation.EventHandler;
import org.fujion.common.DateUtil;
import org.fujion.common.StrUtil;
import org.fujion.component.*;
import org.fujion.event.*;
import org.fujion.page.PageUtil;
import org.fujionclinical.api.security.SecurityUtil;
import org.fujionclinical.fhir.stu3.ui.reporting.common.Constants;
import org.fujionclinical.ui.dialog.PopupDialog;
import org.hl7.fhir.dstu3.model.Identifier;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

/**
 * Class which extends Popup. Subclasses should provide implementation of method addRows(). This is
 * the popup dialog that displays the detail information for a data object in a grid view. If
 * {@link SecurityUtil#hasDebugRole()}, the dataObject is interrogated and the classes get/bean
 * methods are invoked and displayed on the display as well.
 */
public class DrillDownDisplay extends PopupDialog {
    
    private class DebugLink extends DrillDownIconBase<Object> implements IEventListener {
        
        private final String title;
        
        public DebugLink(Object dataObject, String title) {
            super(dataObject, DrillDownDisplay.class);
            this.title = title;
        }
        
        @Override
        public void onEvent(Event event) {
            DrillDownDisplay ddd = new DrillDownDisplay(DrillDownDisplay.this, dataObject, title);
            ddd.setDebug(debug);
            ddd.owner = DrillDownDisplay.this;
            ddd.btnCloseAll.setVisible(true);
            ddd.show();
        }
        
        @Override
        protected void attachEventListener() {
            addEventListener(ClickEvent.TYPE, this);
        }
    }
    
    private static final Log log = LogFactory.getLog(DrillDownDisplay.class);
    
    private Grid grid;
    
    private Column colLabel;
    
    private Column colValue;
    
    private Button btnCloseAll;
    
    private Object dataObject;
    
    private boolean resizing = false;
    
    private DrillDownDisplay owner = null;
    
    private boolean debug = SecurityUtil.hasDebugRole();
    
    /**
     * Subclasses a listbox for displaying multiple values in a single grid cell. Prevents selection
     * of entries in the list by resetting the state to no selection.
     */
    private class MultiListbox extends Listbox implements IEventListener {
        
        private String defaultValue = null;
        
        /**
         * Creates the list box to fill the parent cell. Capture select and click events to undo any
         * selection that might occur.
         *
         * @param defaultValue The default value.
         */
        public MultiListbox(String defaultValue) {
            super();
            this.defaultValue = defaultValue;
            setWidth("100%");
            setHeight("100%");
            addStyle("background", "white");
            addStyle("border", "none");
            setDisabled(true);
            addEventListener(ChangeEvent.TYPE, this);
            addEventListener(ClickEvent.TYPE, this);
        }
        
        /**
         * Reset the list box state to no selection.
         */
        @Override
        public void onEvent(Event event) {
            if (getSelectedIndex() != -1) {
                setSelectedIndex(-1);
            }
            
            return;
        }
        
        /**
         * Add an item to the list box.
         *
         * @param value = Value of item to add.
         */
        public void addItem(String value) {
            Listitem item = new Listitem();
            item.setLabel(value);
            addChild(item);
            
            if (value.equalsIgnoreCase(defaultValue)) {
                item.addStyle("font-style", "italic");
            }
        }
    }
    
    /**
     * Create the dialog.
     *
     * @param parent The parent component.
     * @param dataObject The data object.
     * @param title The dialog title.
     */
    public DrillDownDisplay(BaseComponent parent, Object dataObject, String title) {
        super(parent, title);
        setDataObject(dataObject);
        setWidth("600px");
        
        try {
            PageUtil.createPage(Constants.RESOURCE_PREFIX + "drillDownDisplay.fsp", this);
            adjustGrid();
        } catch (Exception e) {
            log.error("Error creating drilldown display dialog.", e);
        }
    }
    
    /**
     * Subclasses need implement the following method to add rows to the display.
     */
    public void addRows() {
    }
    
    /**
     * Add a link for drilldown of objects.
     *
     * @param dataObject Object for drilldown.
     * @param title Title for dialog.
     */
    private void addLink(Object dataObject, String title) {
        if (debugObject(dataObject, true)) {
            BaseComponent cell = getLastRow().getFirstChild();
            cell.addChild(new DebugLink(dataObject, title));
        }
    }
    
    /**
     * Returns the last row added.
     *
     * @return The last row added.
     */
    private Row getLastRow() {
        return (Row) grid.getRows().getLastChild();
    }
    
    /**
     * When debug is true, dataObject is interrogated and the classes get/bean methods are invoked
     * and displayed on the display as well.
     *
     * @param dataObject Object to interrogate.
     * @param checkOnly If true, only checks to see if the object has additional debug info.
     * @return True if the object is a type for which additional debug info is available..
     */
    private boolean debugObject(Object dataObject, boolean checkOnly) {
        if (dataObject != null) {
            Row row;
            Class<?> clazz = dataObject.getClass();
            
            if (!checkOnly) {
                log.debug("Adding Verbose DrillDown Object Debug Information");
                addRow("-------DEBUG--------", clazz.getName());
                row = getLastRow();
                row.addChild(new Label());
                row.addClass(Constants.SCLASS_DRILLDOWN_GRID);
            }
            
            try {
                Object[] params = null;
                //Method[] methods = clazz.getDeclaredMethods();
                Method[] methods = clazz.getMethods();
                
                if (!(dataObject instanceof String)) {
                    for (Method method : methods) {
                        if (Modifier.PUBLIC == method.getModifiers()) {
                            // Assumes getter methods
                            if (method.getName().startsWith("get") && method.getGenericParameterTypes().length == 0) {
                                if (checkOnly) {
                                    return true;
                                }
                                
                                Object invokedObject = method.invoke(getDataObject(), params);
                                String methodName = method.getName();
                                addRowViaObject(methodName, invokedObject);
                                addLink(invokedObject, clazz.getName() + "." + methodName);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        
        return false;
    }
    
    /**
     * Show the popup dialog, populating it with detail information for the specified data object.
     */
    @Override
    public void show() {
        addRows();
        
        if (debug) {
            debugObject(dataObject, false);
        }
        
        if (grid.getRows().getChildren().size() > 20) {
            grid.setHeight("600px");
        }
        
        super.show();
    }
    
    /**
     * Adds a detail row for a generic object.
     *
     * @param header The row header.
     * @param value The object to add.
     */
    protected void addRowViaObject(String header, Object value) {
        if (value instanceof String) {
            addRow(header, (String) value);
        } else if (value instanceof Date) {
            addRow(header, (Date) value);
        } else if (value instanceof Identifier) {
            addRow(header, ((Identifier) value).getValue());
        } else {
            addRow(header, value == null ? "" : String.valueOf(value));
        }
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column). If
     * log.isDebugEnabled() is false then don't add row for empty or null values
     *
     * @param header Text for header column
     * @param value Text for value column
     */
    protected void addRow(String header, String value) {
        if ((value == null || value.length() == 0) && !debug) {
            return;
        }
        
        Label lbl = new Label();
        lbl.setLabel(value);
        lbl.setHint(value);
        addRow(header, lbl);
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column).
     *
     * @param header Text for header column
     * @param value Text for value column
     */
    protected void addRow(String header, Integer value) {
        addRow(header, value == null ? "" : Integer.toString(value));
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column).
     *
     * @param header Text for header column
     * @param value Text for value column
     */
    protected void addRow(String header, Long value) {
        addRow(header, value == null ? "" : Long.toString(value));
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column).
     *
     * @param header Text for header column
     * @param value Date object
     */
    protected void addRow(String header, Date value) {
        try {
            addRow(header, DateUtil.formatDate(value));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            addRow(header, e.getMessage());
        }
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column).
     *
     * @param header Text for header column
     * @param value Concept object
     */
    protected void addRow(String header, Identifier value) {
        addRow(header, value == null ? "" : value.getValue().toString());
    }
    
    /**
     * Add a row containing a multi-valued list.
     *
     * @param header Text for header column
     * @param list Iterable object of string values
     * @param dflt Value to mark as default
     */
    protected void addRow(String header, Iterable<String> list, String dflt) {
        if (list == null) {
            return;
        }
        
        MultiListbox container = new MultiListbox(dflt);
        
        for (String val : list) {
            container.addItem(val);
        }
        
        if (container.getChildCount() > 0 || debug) {
            addRow(header, container);
        }
    }
    
    /**
     * Add a row containing the specified header (left column) and value container (right column).
     *
     * @param header Text for header column
     * @param container Object containing text value(s)
     */
    protected void addRow(String header, BaseComponent container) {
        Row row = new Row();
        grid.getRows().addChild(row);
        Div div = new Div();
        Label label = new Label(header + ":");
        label.addStyle("font-weight", "bold");
        label.addStyle("word-wrap", "word-break");
        row.addChild(div);
        row.addChild(label);
        row.addChild(container);
    }
    
    public void onClick$btnClose() {
        detach();
    }
    
    public void onClick$btnCloseAll() {
        detach();
        
        if (owner != null) {
            owner.onClick$btnCloseAll();
        }
    }
    
    @EventHandler("resize")
    private void onResize(ResizeEvent event) {
        if (!resizing) {
            try {
                resizing = true;
                adjustGrid();
            } finally {
                resizing = false;
            }
        }
    }
    
    private void adjustGrid() {
        int w = (StrUtil.extractInt(getWidth()) - 40) / 3;
        colLabel.setWidth(w + "px");
        colValue.setWidth((w * 2) + "px");
    }
    
    public Grid getGrid() {
        return grid;
    }
    
    public Object getDataObject() {
        return dataObject;
    }
    
    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public boolean isDebug() {
        return debug;
    }
    
}
