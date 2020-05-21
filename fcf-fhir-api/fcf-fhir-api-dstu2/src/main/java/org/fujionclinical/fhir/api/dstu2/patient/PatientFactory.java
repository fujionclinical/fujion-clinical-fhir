package org.fujionclinical.fhir.api.dstu2.patient;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.dstu2.common.ResourceFactory;

public class PatientFactory extends ResourceFactory<IPatient, Patient> {

    public PatientFactory(IGenericClient fhirClient) {
        super(fhirClient, IPatient.class, Patient.class);
    }

    @Override
    protected IPatient wrapResource(Patient resource) {
        return new PatientWrapper(resource);
    }
}
