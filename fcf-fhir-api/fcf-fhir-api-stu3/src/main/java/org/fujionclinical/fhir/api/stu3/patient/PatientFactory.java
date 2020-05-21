package org.fujionclinical.fhir.api.stu3.patient;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.stu3.common.ResourceFactory;
import org.hl7.fhir.dstu3.model.Patient;

public class PatientFactory extends ResourceFactory<IPatient, Patient> {

    public PatientFactory(IGenericClient fhirClient) {
        super(fhirClient, IPatient.class, Patient.class);
    }

    @Override
    protected IPatient wrapResource(Patient resource) {
        return new PatientWrapper(resource);
    }
}