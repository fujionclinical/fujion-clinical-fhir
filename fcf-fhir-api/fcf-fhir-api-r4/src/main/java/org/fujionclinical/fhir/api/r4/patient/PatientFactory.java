package org.fujionclinical.fhir.api.r4.patient;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.r4.common.ResourceFactory;
import org.hl7.fhir.r4.model.Patient;

public class PatientFactory extends ResourceFactory<IPatient, Patient> {

    public PatientFactory(IGenericClient fhirClient) {
        super(fhirClient, IPatient.class, Patient.class);
    }

    @Override
    protected IPatient wrapResource(Patient resource) {
        return PatientWrapper.create(resource);
    }
}
