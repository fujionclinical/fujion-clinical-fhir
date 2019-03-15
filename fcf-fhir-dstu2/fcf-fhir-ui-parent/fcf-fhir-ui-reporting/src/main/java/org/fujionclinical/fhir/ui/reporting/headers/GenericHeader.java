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
package org.fujionclinical.fhir.ui.reporting.headers;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.fujion.annotation.OnFailure;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.DateUtil;
import org.fujion.component.BaseComponent;
import org.fujion.component.Label;
import org.fujionclinical.api.context.UserContext;
import org.fujionclinical.api.domain.IUser;
import org.fujionclinical.api.event.IGenericEvent;
import org.fujionclinical.fhir.api.common.FhirUtil;
import org.fujionclinical.fhir.api.patient.PatientContext;
import org.fujionclinical.ui.controller.FrameworkController;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;

/**
 * This is the generic controller for the stock report headers.
 */
public class GenericHeader extends FrameworkController {
    
    @WiredComponent(onFailure = OnFailure.IGNORE)
    private Label lblPatientInfo;
    
    @WiredComponent(onFailure = OnFailure.IGNORE)
    private Label lblUserInfo;
    
    @WiredComponent(onFailure = OnFailure.IGNORE)
    private Label lblTimestamp;
    
    @WiredComponent(onFailure = OnFailure.IGNORE)
    private Label lblTitle;
    
    private final String contextEvent;
    
    private final IGenericEvent<?> eventListener = (x, y) -> {
        refresh();
    };
    
    public GenericHeader() {
        this(null);
    }
    
    public GenericHeader(String contextEvent) {
        super();
        this.contextEvent = contextEvent;
    }
    
    /**
     * Creates an annotation binder for the controller.
     *
     * @param comp The component.
     */
    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        
        if (contextEvent != null) {
            subscribe(contextEvent, true);
        }
        
        refresh();
    }
    
    /**
     * Retrieves a formatted header for the current patient.
     *
     * @return Formatted header.
     */
    public String getPatientInfo() {
        Patient patient = PatientContext.getActivePatient();
        String text;
        
        if (patient == null) {
            text = "No Patient Selected";
        } else {
            Identifier mrn = FhirUtil.getMRN(patient); // May be null!
            text = FhirUtil.formatName(patient.getName());
            
            if (mrn != null) {
                text += "  #" + mrn.getValue();
            }
            
            String gender = patient.hasGender() ? patient.getGender().getDisplay() : "";
            
            if (!StringUtils.isEmpty(gender)) {
                text += "   (" + gender + ")";
            }
            
            Date deceased = patient.getDeceased() instanceof DateType ? ((DateType) patient.getDeceased()).getValue() : null;
            String age = DateUtil.formatAge(patient.getBirthDate(), true, deceased);
            text += "  Age: " + age;
            
            if (deceased != null) {
                text += "  Died: " + DateUtil.formatDate(deceased);
            }
        }
        
        return text;
    }
    
    /**
     * Retrieves a formatted header for the current user.
     *
     * @return Formatted header.
     */
    public String getUserInfo() {
        IUser user = UserContext.getActiveUser();
        return user == null ? "No User Selected" : user.getFullName();
    }
    
    /**
     * Returns the current date in standard format.
     *
     * @return Timestamp for current date.
     */
    public String getTimestamp() {
        return DateUtil.formatDate(DateUtil.stripTime(new Date()));
    }
    
    /**
     * Rebind form data when context changes.
     */
    @Override
    public void refresh() {
        updateLabel(lblPatientInfo, getPatientInfo());
        updateLabel(lblUserInfo, getUserInfo());
        updateLabel(lblTimestamp, getTimestamp());
    }
    
    private void updateLabel(Label label, String value) {
        if (label != null) {
            label.setLabel(value);
        }
    }
    
    /**
     * Subscribe or unsubscribe from context change event.
     *
     * @param eventName The event name.
     * @param subscribe If true, subscribe; if false, unsubscribe.
     */
    private void subscribe(String eventName, boolean subscribe) {
        if (subscribe) {
            getEventManager().subscribe(eventName, eventListener);
        } else {
            getEventManager().unsubscribe(eventName, eventListener);
        }
    }
    
}
