package org.fujionclinical.fhir.api.r5.patient;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.r5.common.BaseResourceDAO;
import org.hl7.fhir.r5.model.Patient;

public class PatientDAO extends BaseResourceDAO<IPatient, Patient> {

    public PatientDAO(IGenericClient fhirClient) {
        super(fhirClient, IPatient.class, Patient.class);
    }

    @Override
    protected IPatient wrapResource(Patient resource) {
        return PatientWrapper.wrap(resource);
    }
}
