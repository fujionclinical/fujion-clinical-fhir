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
package org.fujionclinical.fhir.lib.patientselection.stu3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.common.DateUtil;
import org.fujion.component.Listitem;
import org.fujionclinical.api.FrameworkUtil;
import org.fujionclinical.fhir.lib.patientselection.common.Constants;
import org.fujionclinical.fhir.lib.patientselection.common.Features;
import org.fujionclinical.ui.controller.FrameworkController;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Controller for patient matches dialog.
 */
public class PatientMatchesController extends FrameworkController {
    
    private static final Log log = LogFactory.getLog(PatientMatchesController.class);
    
    private final DOBComparator dobComparatorAsc = new DOBComparator(true);
    
    private final DOBComparator dobComparatorDsc = new DOBComparator(false);
    
    private final Features features = Features.getInstance();
    
    /**
     * Comparator for sorting by date of birth.
     */
    private class DOBComparator implements Comparator<Listitem> {
        
        private final boolean ascending;
        
        DOBComparator(boolean ascending) {
            super();
            this.ascending = ascending;
        }
        
        @Override
        public int compare(Listitem o1, Listitem o2) {
            if (log.isDebugEnabled()) {
                log.debug("Listitem1: " + o1 + ", Listitem2: " + o2);
            }
            Patient pat1 = o1.getData(Patient.class);
            Patient pat2 = o2.getData(Patient.class);
            int result = DateUtil.compare(pat1.getBirthDate(), pat2.getBirthDate());
            return ascending ? result : -result;
        }
        
    }
    
    /**
     * Returns the feature map for use by EL to determine if a given feature is enabled.
     * 
     * @return The feature map.
     */
    public Map<String, Boolean> getFeatureEnabled() {
        return features.getFeatureMap();
    }
    
    /**
     * Returns the date of birth ascending comparator.
     * 
     * @return Ascending DOB comparator.
     */
    public DOBComparator getDOBComparatorAsc() {
        return dobComparatorAsc;
    }
    
    /**
     * Returns the date of birth descending comparator.
     * 
     * @return Descending DOB comparator.
     */
    public DOBComparator getDOBComparatorDsc() {
        return dobComparatorDsc;
    }
    
    /**
     * Closes the dialog and returns the selected patient to the caller.
     * 
     * @param patient Patient selected by the user.
     */
    public void selectPatient(Object patient) {
        FrameworkUtil.setAttribute(Constants.RESULT_ATTRIB, patient);
        root.detach();
    }
    
    /**
     * Returns the patient list passed from the caller.
     * 
     * @return Patient list.
     */
    @SuppressWarnings("unchecked")
    public List<Patient> getResults() {
        return (List<Patient>) FrameworkUtil.getAttribute(Constants.RESULT_ATTRIB);
    }
    
    /**
     * Returns the size of the patient list.
     * 
     * @return Patient list size.
     */
    public int getResultCount() {
        return getResults().size();
    }
}
