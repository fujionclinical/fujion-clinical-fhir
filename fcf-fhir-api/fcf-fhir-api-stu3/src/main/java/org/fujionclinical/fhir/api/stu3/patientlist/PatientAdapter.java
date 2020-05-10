package org.fujionclinical.fhir.api.stu3.patientlist;

import org.fujionclinical.fhir.api.common.patientlist.IPatientAdapter;
import org.fujionclinical.fhir.api.stu3.common.FhirUtil;
import org.fujionclinical.fhir.api.stu3.patient.PatientContext;
import org.hl7.fhir.dstu3.model.Patient;

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
