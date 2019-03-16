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
package org.fujionclinical.fhir.stu3.ui.patientselection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.ancillary.IResponseCallback;
import org.fujion.common.StrUtil;
import org.fujionclinical.fhir.dstu3.api.common.FhirUtil;
import org.fujionclinical.fhir.common.query.SearchException;
import org.fujionclinical.fhir.dstu3.api.patient.PatientSearchCriteria;
import org.fujionclinical.fhir.dstu3.api.patient.PatientUtil;
import org.fujionclinical.ui.dialog.DialogUtil;
import org.fujionclinical.ui.util.FCFUtil;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.Comparator;
import java.util.List;

/**
 * Patient search services.
 */
public class PatientSearchUtil {
    
    private static final Log log = LogFactory.getLog(PatientSearchUtil.class);
    
    private static final String ERROR_PATIENT_NOT_FOUND = "@patientsearch.error.patient.not.found";
    
    private static final String UNEXPECTED_ERROR = "@patientsearch.error.unknown";
    
    private static final String TEXT_TOO_MANY_MATCHES = "@patientsearch.warn.too.many.matches.text";
    
    private static final String TITLE_TOO_MANY_MATCHES = "@patientsearch.warn.too.many.matches.title";
    
    private static final String BTN_REFINE_LABEL = "@patientsearch.btn.refine.label";
    
    private static final String BTN_CONTINUE_LABEL = "@patientsearch.btn.continue_.label";
    
    private static final String REFINE_BUTTONS = BTN_REFINE_LABEL + "|" + BTN_CONTINUE_LABEL;
    
    private static final Comparator<Patient> patientComparator = new Comparator<Patient>() {
        
        /**
         * Sort by patient full name, ignoring case.
         *
         * @param patient1 First patient to compare.
         * @param patient2 Second patient to compare.
         * @return Result of comparison.
         */
        @Override
        public int compare(Patient patient1, Patient patient2) {
            HumanName name1 = FhirUtil.getName(patient1.getName(), NameUse.USUAL, null);
            HumanName name2 = FhirUtil.getName(patient2.getName(), NameUse.USUAL, null);
            String cmp1 = name1 == null ? "" : name1.toString();
            String cmp2 = name2 == null ? "" : name2.toString();
            return cmp1.compareToIgnoreCase(cmp2.toString());
        }
        
    };
    
    /**
     * Perform search, using the specified search text.
     *
     * @param searchText Text to use in search.
     * @param maxMatches Maximum number of allowable matches. If this value is exceeded, the user
     *            will be given the opportunity to cancel the search. A value of zero suppresses
     *            this feature.
     * @param callback Callback to receive a list of patients matching the specified search
     *            criteria. The return value will be null if no search criteria are provided or the
     *            search exceeds the maximum allowable matches and the user chooses to cancel the
     *            search.
     */
    public static void execute(String searchText, int maxMatches, IResponseCallback<List<Patient>> callback) {
        execute(new PatientSearchCriteria(searchText), maxMatches, callback);
    }
    
    /**
     * Perform search, using the specified criteria.
     *
     * @param criteria The search criteria.
     * @param maxMatches Maximum number of allowable matches. If this value is exceeded, the user
     *            will be given the opportunity to cancel the search. A value of zero suppresses
     *            this feature.
     * @param callback Callback to receive a list of patients matching the specified search
     *            criteria. The return value will be null if no search criteria are provided or the
     *            search exceeds the maximum allowable matches and the user chooses to cancel the
     *            search.
     */
    public static void execute(PatientSearchCriteria criteria, int maxMatches, IResponseCallback<List<Patient>> callback) {
        if (criteria == null || criteria.isEmpty()) {
            doCallback(null, callback);
            return;
        }
        
        try {
            criteria.validate();
            List<Patient> matches = PatientUtil.search(criteria);
            
            if (matches == null || matches.size() == 0) {
                throw new SearchException(ERROR_PATIENT_NOT_FOUND);
            }
            
            if (maxMatches > 0 && matches.size() > maxMatches) {
                String msg = StrUtil.formatMessage(TEXT_TOO_MANY_MATCHES, matches.size());
                DialogUtil.prompt(msg, TITLE_TOO_MANY_MATCHES, REFINE_BUTTONS, (response) -> {
                    if (response.hasResponse(BTN_REFINE_LABEL)) {
                        matches.clear();
                    }

                    doCallback(matches, callback);
                });
                
                return;
            }
            
            //PatientContext.checkRequired(matches);
            doCallback(matches, callback);
        } catch (SearchException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during patient search.", e);
            throw new SearchException(StrUtil.formatMessage(UNEXPECTED_ERROR, FCFUtil.formatExceptionForDisplay(e)), e);
        }
    }
    
    private static void doCallback(List<Patient> matches, IResponseCallback<List<Patient>> callback) {
        if (callback != null) {
            if (matches != null) {
                matches.sort(patientComparator);
            }
            
            callback.onComplete(matches);
        }
    }
    
    /**
     * Enforce static class.
     */
    private PatientSearchUtil() {
    }
}
