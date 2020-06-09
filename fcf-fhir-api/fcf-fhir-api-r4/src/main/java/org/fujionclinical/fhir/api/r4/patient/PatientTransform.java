package org.fujionclinical.fhir.api.r4.patient;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.patient.IPatient;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.BeanUtils;

public class PatientTransform implements IWrapperTransform<IPatient, Patient> {

    public static final PatientTransform instance = new PatientTransform();

    @Override
    public Patient _unwrap(IPatient value) {
        Patient wrapped = new Patient();
        IPatient patient = wrap(wrapped);
        BeanUtils.copyProperties(value, patient);
        return wrapped;
    }

    @Override
    public IPatient _wrap(Patient value) {
        return new PatientWrapper(value);
    }

}