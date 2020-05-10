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
package org.fujionclinical.fhir.api.stu3.patient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.context.ContextItems;
import org.fujionclinical.api.context.ContextManager;
import org.fujionclinical.api.context.IContextEvent;
import org.fujionclinical.fhir.api.stu3.common.FhirUtil;
import org.fujionclinical.fhir.api.stu3.common.ResourceContext;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;

/**
 * Wrapper for shared patient context.
 */
public class PatientContext extends ResourceContext<Patient> {


    private static final Log log = LogFactory.getLog(PatientContext.class);

    protected static final String SUBJECT_NAME = "Patient";

    protected static final String CCOW_ID = SUBJECT_NAME + ".Id";

    protected static final String CCOW_MRN = CCOW_ID + ".MRN";

    protected static final String CCOW_MPI = CCOW_ID + ".MPI";

    protected static final String CCOW_CO = SUBJECT_NAME + ".Co";

    protected static final String CCOW_SEX = CCOW_CO + ".Sex";

    protected static final String CCOW_DOB = CCOW_CO + ".DateTimeOfBirth";

    protected static final String CCOW_NAM = CCOW_CO + ".PatientName";

    public interface IPatientContextEvent extends IContextEvent {
    }

    /**
     * Returns the managed patient context.
     *
     * @return Patient context.
     */
    public static PatientContext getPatientContext() {
        return (PatientContext) ContextManager.getInstance().getSharedContext(PatientContext.class.getName());
    }

    /**
     * Request a patient context change.
     *
     * @param patient New patient.
     */
    public static void changePatient(Patient patient) {
        try {
            getPatientContext().requestContextChange(patient);
        } catch (Exception e) {
            log.error("Error during patient context change.", e);
        }
    }

    /**
     * Request a patient context change.
     *
     * @param logicalId Logical id of the patient.
     */
    public static void changePatient(String logicalId) {
        getPatientContext().requestContextChange(logicalId);
    }

    /**
     * Returns the patient in the current context.
     *
     * @return Patient object (may be null).
     */
    public static Patient getActivePatient() {
        return getPatientContext().getContextObject(false);
    }

    /**
     * Create a shared patient context with an initial null state.
     */
    public PatientContext() {
        this(null);
    }

    /**
     * Create a shared patient context with a specified initial state.
     *
     * @param patient Patient that will be the initial state.
     */
    public PatientContext(Patient patient) {
        super(SUBJECT_NAME, Patient.class, IPatientContextEvent.class, patient);
    }

    /**
     * Creates a CCOW context from the specified patient object.
     */
    @Override
    public ContextItems toCCOWContext(Patient patient) {
        Identifier mrn = FhirUtil.getMRN(patient);
        contextItems.setItem(CCOW_MRN, mrn == null ? null : mrn.getValue(), "MRN");
        contextItems.setItem(CCOW_NAM, patient.getName());
        contextItems.setItem(CCOW_SEX, patient.getGender());
        contextItems.setItem(CCOW_DOB, patient.getBirthDate());
        return contextItems;
    }

    /**
     * Returns a patient object based on the specified CCOW context.
     */
    @Override
    public Patient fromCCOWContext(ContextItems contextItems) {
        return null;
    }

    /**
     * Returns a priority value of 10.
     *
     * @return Priority value for context manager.
     */
    @Override
    public int getPriority() {
        return 10;
    }
}
