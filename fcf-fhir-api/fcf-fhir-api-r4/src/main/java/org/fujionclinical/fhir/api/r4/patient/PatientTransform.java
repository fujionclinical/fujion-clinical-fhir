package org.fujionclinical.fhir.api.r4.patient;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.patient.IPatient;
import org.hl7.fhir.r4.model.Patient;

public class PatientTransform implements IWrapperTransform<IPatient, Patient> {

    public static final PatientTransform instance = new PatientTransform();

    @Override
    public IPatient _wrap(Patient value) {
        return new PatientWrapper(value);
    }

    @Override
    public Patient newWrapped() {
        return new Patient();
    }

}
