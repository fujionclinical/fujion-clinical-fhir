package org.fujionclinical.fhir.api.r4.patientlist;

import org.fujionclinical.fhir.api.common.patientlist.IPatientAdapter;
import org.fujionclinical.fhir.api.common.patientlist.IPatientAdapterConsumer;
import org.fujionclinical.fhir.api.common.patientlist.IPatientAdapterFactory;
import org.fujionclinical.fhir.api.r4.common.DomainFactory;
import org.fujionclinical.fhir.api.r4.patient.PatientContext;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Patient;

public class PatientAdapterFactory implements IPatientAdapterFactory {

    @Override
    public IPatientAdapter create(String logicalId) {
        return new PatientAdapter(DomainFactory.getInstance().fetchObject(Patient.class, logicalId));
    }

    @Override
    public IPatientAdapter create(IBaseResource resource) {
        return new PatientAdapter((Patient) resource);
    }

    @Override
    public void registerListener(IPatientAdapterConsumer consumer) {
        PatientContext.getPatientContext().addListener((eventName, patient) -> consumer.accept(new PatientAdapter(patient)));
    }
}
