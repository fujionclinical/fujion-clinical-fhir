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
package org.fujionclinical.fhir.lib.reports.r4.headers;

import org.apache.commons.lang3.StringUtils;
import org.fujion.annotation.OnFailure;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.DateUtil;
import org.fujion.component.Label;
import org.fujionclinical.fhir.lib.reports.common.Constants;
import org.fujionclinical.fhir.r4.api.common.FhirUtil;
import org.fujionclinical.fhir.r4.api.patient.PatientContext;
import org.fujionclinical.ui.reports.header.ReportHeaderBase;
import org.fujionclinical.ui.reports.header.ReportHeaderRegistry;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

import java.util.Date;

/**
 * This is the generic controller for the stock report headers.
 */
public class ReportHeaderPatient extends ReportHeaderBase {

    static {
        ReportHeaderRegistry.getInstance().register("patient", Constants.RESOURCE_PREFIX + "patientReportHeader.fsp");
    }

    @WiredComponent(onFailure = OnFailure.IGNORE)
    private Label lblPatientInfo;
    
    public ReportHeaderPatient() {
        super("CONTEXT.CHANGED.Patient");
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
     * Rebind form data when context changes.
     */
    @Override
    public void refresh() {
        super.refresh();
        updateLabel(lblPatientInfo, getPatientInfo());
    }
    
}
