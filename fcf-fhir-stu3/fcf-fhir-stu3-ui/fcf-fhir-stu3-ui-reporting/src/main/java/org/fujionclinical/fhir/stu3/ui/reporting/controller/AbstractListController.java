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

import org.fujion.annotation.EventHandler;
import org.fujion.annotation.OnFailure;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.Listbox;
import org.fujion.component.Listitem;
import org.fujionclinical.api.query.IQueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a stateful controller that supports list-based displays.
 *
 * @param <T> Query result class
 * @param <M> Model result class
 */
public abstract class AbstractListController<T, M> extends AbstractBaseController<T, M> {

    @WiredComponent
    protected Listbox listbox;

    public AbstractListController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet) {
        super(service, labelPrefix, propertyPrefix, printStyleSheet);
    }

    public AbstractListController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet, boolean patientAware) {
        super(service, labelPrefix, propertyPrefix, printStyleSheet, patientAware);
    }

    /**
     * Initializes the controller. Loads user preferences and properties.
     */
    @Override
    protected void initializeController() {
        setComponents(listbox, listbox);
        super.initializeController();
    }

    /**
     * Clear the current selection, if any.
     */
    protected void clearSelection() {
        listbox.clearSelected();
    }

    /**
     * Returns a list of listbox items.
     *
     * @param selectedOnly If true, only selected items are returned.
     * @return List of list items.
     */
    protected Iterable<Listitem> getItems(boolean selectedOnly) {
        return selectedOnly ? listbox.getSelected() : listbox.getChildren(Listitem.class);
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
        Iterable<Listitem> items = getItems(selectedOnly);
        List<M> objects = new ArrayList<>();

        for (Listitem item : items) {
            objects.add((M) item.getData());
        }

        return objects;
    }

    /**
     * Clear selected items
     */
    @EventHandler(value = "click", target = "btnClear", onFailure = OnFailure.IGNORE)
    private void onClick$btnClear() {
        clearSelection();
    }

    public Listbox getListbox() {
        return listbox;
    }
}
