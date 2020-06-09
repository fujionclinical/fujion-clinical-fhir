package org.fujionclinical.fhir.api.dstu2.patient;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.patient.IPatient;

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
