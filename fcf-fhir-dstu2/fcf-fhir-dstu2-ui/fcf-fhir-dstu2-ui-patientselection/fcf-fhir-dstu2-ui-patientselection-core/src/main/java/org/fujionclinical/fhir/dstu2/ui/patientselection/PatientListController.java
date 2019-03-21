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
package org.fujionclinical.fhir.dstu2.ui.patientselection;

import org.fujion.annotation.EventHandler;
import org.fujion.component.BaseComponent;
import org.fujion.component.Grid;
import org.fujion.component.Rows;
import org.fujion.event.Event;
import org.fujion.event.IEventListener;
import org.fujion.model.ListModel;
import org.fujionclinical.api.spring.SpringUtil;
import org.fujionclinical.fhir.dstu2.api.patientlist.IPatientList;
import org.fujionclinical.fhir.dstu2.api.patientlist.PatientListItem;
import org.fujionclinical.ui.controller.FrameworkController;

import java.util.Collection;

/**
 * Controller for patient list display. Recognizes the following dynamic properties: patientList =
 * The patient list to display (bean id or an instance of IPatientList). eventListener = The event
 * listener to handle selection from the list. maxRows = The maximum number of rows to display.
 * Defaults to 8.
 */
public class PatientListController extends FrameworkController {

    public static final String ATTR_PATIENT_LIST = "patientList";

    public static final String ATTR_EVENT_LISTENER = "eventListener";

    public static final String ATTR_MAX_ROWS = "maxRows";

    private Grid patientList;

    private IEventListener selectListener;

    /**
     * Set up the list box based on dynamic properties passed via the execution.
     *
     * @param comp The top level component.
     */
    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        IPatientList plist = getPatientList();
        Rows rows = patientList.getRows();
        rows.setRenderer(new PatientListItemRenderer(patientList));
        Collection<PatientListItem> items = plist.getListItems();
        rows.setModel(new ListModel<>(items));
        selectListener = comp.getAttribute(ATTR_EVENT_LISTENER, IEventListener.class);
    }

    /**
     * Returns the maximum rows from the "maxRows" dynamic property. If none specified, defaults to
     * 8.
     *
     * @return Maximum rows.
     */
    @SuppressWarnings("unused")
    private int getMaxRows() {
        return patientList.getAttribute(ATTR_MAX_ROWS, 8);
    }

    /**
     * Returns the patient list from the "patientList" dynamic property.
     *
     * @return The patient list.
     */
    private IPatientList getPatientList() {
        Object plist = patientList.getAttribute(ATTR_PATIENT_LIST);

        if (plist instanceof String) {
            return SpringUtil.getBean((String) plist, IPatientList.class);
        } else if (plist instanceof IPatientList) {
            return (IPatientList) plist;
        } else {
            return null;
        }
    }

    /**
     * Pass selection event to external listener, if any.
     *
     * @param event The onSelect event.
     */
    @EventHandler(value = "change", target = "rows")
    private void onSelect$patientList(Event event) {
        if (selectListener != null) {
            selectListener.onEvent(event);
        }
    }

}
