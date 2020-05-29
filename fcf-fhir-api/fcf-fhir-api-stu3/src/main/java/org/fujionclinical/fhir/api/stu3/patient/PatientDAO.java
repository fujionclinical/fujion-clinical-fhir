package org.fujionclinical.fhir.api.stu3.patient;

import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.common.core.AbstractFhirService;
import org.fujionclinical.fhir.api.stu3.common.BaseResourceDAO;
import org.hl7.fhir.dstu3.model.Patient;

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
