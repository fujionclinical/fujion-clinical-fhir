package org.fujionclinical.fhir.api.dstu2.patientlist;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.fujionclinical.fhir.api.common.patientlist.IPatientAdapter;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtil;
import org.fujionclinical.fhir.api.dstu2.patient.PatientContext;

import java.util.Date;

public class PatientAdapter implements IPatientAdapter {

    private final Patient patient;

    public PatientAdapter(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String getName() {
        return FhirUtil.formatName(patient.getName());
    }

    @Override
    public Date getDOB() {
        return patient.getBirthDate();
    }

    @Override
    public String getMRN() {
        return FhirUtil.getMRNString(patient);
    }

    @Override
    public Patient getResource() {
        return patient;
    }

    @Override
    public void select() {
        PatientContext.changePatient(patient);
    }
}
