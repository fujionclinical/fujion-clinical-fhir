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
package org.fujionclinical.fhir.lib.patientselection.core.r5;

import org.fujion.ancillary.IResponseCallback;
import org.fujion.common.StrUtil;
import org.fujion.component.Window;
import org.fujion.page.PageUtil;
import org.fujionclinical.api.FrameworkUtil;
import org.fujionclinical.api.property.PropertyUtil;
import org.fujionclinical.api.security.SecurityUtil;
import org.fujionclinical.fhir.lib.patientselection.common.Constants;
import org.fujionclinical.fhir.r5.api.patient.PatientContext;
import org.fujionclinical.ui.dialog.DialogUtil;
import org.hl7.fhir.r5.model.Patient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is the convenience class for accessing patient selectors.
 */
public class PatientSelection {
    
    /**
     * Returns the current patient selector. If one has not already been created, it is created from
     * the factory.
     *
     * @return The patient selector.
     */
    private static IPatientSelector getSelector() {
        IPatientSelector selector = (IPatientSelector) FrameworkUtil.getAttribute(Constants.SELECTOR_ATTRIB);
        
        if (selector == null) {
            IPatientSelectorFactory factory = getFactory();
            selector = factory == null ? null : factory.create();
            FrameworkUtil.setAttribute(Constants.SELECTOR_ATTRIB, selector);
        }
        
        return selector;
    }
    
    /**
     * Returns the patient selector factory based on the PATIENT.SELECTION.SELECTOR property. If
     * this property is not set, the first registered factory is returned.
     *
     * @return The patient selector factory.
     */
    private static IPatientSelectorFactory getFactory() {
        String factoryBeanId;
        IPatientSelectorFactory factory;
        PatientSelectorRegistry registry = PatientSelectorRegistry.getInstance();
        
        try {
            factoryBeanId = PropertyUtil.getValue("PATIENT.SELECTION.SELECTOR");
        } catch (Exception e) {
            factoryBeanId = null;
        }
        
        factory = factoryBeanId == null ? null : registry.get(factoryBeanId);
        
        if (factory == null) {
            Iterator<IPatientSelectorFactory> iterator = registry.iterator();
            
            if (iterator.hasNext()) {
                factory = iterator.next();
            } else {
                DialogUtil.showError("@patientselection.error.no.selectors");
            }
        }
        
        return factory;
    }
    
    /**
     * Displays the new patient selection dialog.
     *
     * @param changeContext If true, request a context change when a patient is selected.
     */
    public static void show(boolean changeContext) {
        show(changeContext, null);
    }
    
    /**
     * Displays the new patient selection dialog.
     *
     * @param changeContext If true, request a context change when a patient is selected.
     * @param callback Returns the selected patient at the time the dialog was closed. This may be
     *            different from the patient in the shared context if <b>noContextChange</b> was
     *            true or the requested context change was rejected. It will be null if no patient
     *            was selected when the dialog was closed or if the selection was canceled by the
     *            user.
     */
    public static void show(boolean changeContext, IResponseCallback<Patient> callback) {
        if (canSelect(true)) {
            IPatientSelector selector = getSelector();
            
            if (selector != null) {
                selector.select(patient -> {
                    if (patient != null) {
                        PatientContext.changePatient(patient);
                    }
                    
                    execCallback(callback, patient);
                });
                return;
            }
        }
        
        execCallback(callback, null);
    }
    
    private static void execCallback(IResponseCallback<Patient> callback, Patient patient) {
        if (callback != null) {
            callback.onComplete(patient);
        }
    }
    
    /**
     * Invokes the patient match dialog, displaying the specified list of patients.
     *
     * @param patientList List of patients from which to select.
     * @param callback Callback to receive the patient selected by the user or null if the operation
     *            was canceled.
     */
    public static void selectFromList(List<Patient> patientList, IResponseCallback<Patient> callback) {
        Map<String, Object> args = new HashMap<>();
        args.put(Constants.RESULT_ATTRIB, patientList);
        Window window = (Window) PageUtil.createPage(Constants.RESOURCE_PATH + "patientMatches.fsp", null, args).get(0);
        
        window.modal(callback == null ? null : (event) -> callback.onComplete(window.getAttribute(Constants.RESULT_ATTRIB, Patient.class)));
    }
    
    /**
     * Returns true if this user has patient selection privilege.
     *
     * @param showMessage If true and the user does not have the required privilege, displays an
     *            error dialog.
     * @return True if user can select patients.
     */
    public static boolean canSelect(boolean showMessage) {
        boolean result = SecurityUtil.isGranted("PRIV_PATIENT_SELECT");
        
        if (!result && showMessage) {
            DialogUtil.showError(StrUtil.getLabel(Constants.LBL_CANNOT_SELECT_MESSAGE),
                StrUtil.getLabel(Constants.LBL_CANNOT_SELECT_TITLE));
        }
        
        return result;
    }
    
    /**
     * Returns true if a patient selection should be forced upon login.
     *
     * @return True if patient selection should be forced.
     */
    public static boolean forcePatientSelection() {
        
        try {
            return canSelect(false) && StrUtil.toBoolean(PropertyUtil.getValue("FCF.PATIENT.FORCE.SELECT", null));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Enforce static class.
     */
    private PatientSelection() {
    }
    
}
