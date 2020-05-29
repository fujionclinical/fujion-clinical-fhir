package org.fujionclinical.fhir.api.dstu2.patient;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.common.core.AbstractFhirService;
import org.fujionclinical.fhir.api.dstu2.common.BaseResourceDAO;

public class PatientDAO extends BaseResourceDAO<IPatient, Patient> {

    public PatientDAO(AbstractFhirService fhirService) {
        super(fhirService, IPatient.class, Patient.class);
    }

    @Override
    protected IPatient convert(Patient resource) {
        return PatientWrapper.wrap(resource);
    }

    @Override
    protected Patient convert(IPatient domainResource) {
        return PatientWrapper.unwrap(domainResource);
    }

}
