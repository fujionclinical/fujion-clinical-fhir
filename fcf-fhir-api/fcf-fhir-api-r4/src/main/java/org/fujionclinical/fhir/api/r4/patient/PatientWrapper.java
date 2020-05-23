package org.fujionclinical.fhir.api.r4.patient;

import org.fujionclinical.api.model.*;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.r4.common.FhirUtilR4;
import org.fujionclinical.fhir.api.r4.common.IdentifierWrapper;
import org.fujionclinical.fhir.api.r4.common.PersonNameWrapper;
import org.fujionclinical.fhir.api.r4.terminology.Constants;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

import java.util.Date;
import java.util.List;

public class PatientWrapper implements IPatient, IWrapper<Patient> {

    public static PatientWrapper create(Patient patient) {
        return patient == null ? null : new PatientWrapper(patient);
    }

    private final Patient patient;

    private final List<IPersonName> names;

    private IdentifierWrapper mrn;

    private PatientWrapper(Patient patient) {
        this.patient = patient;
        names = PersonNameWrapper.wrap(patient.getName());
        mrn = IdentifierWrapper.create(FhirUtilR4.getMRN(patient));
    }

    @Override
    public IIdentifier getMRN() {
        return mrn;
    }

    @Override
    public IPatient setMRN(IIdentifier mrn) {
        if (mrn == null) {
            Identifier ident = IdentifierWrapper.unwrap(mrn);
            ident.getType().addCoding(Constants.CODING_MRN);
            this.mrn = IdentifierWrapper.create(new Identifier());
        }

        return this;
    }

    @Override
    public Gender getGender() {
        return FhirUtilR4.convertEnum(patient.getGender(), Gender.class, Gender.OTHER);
    }

    @Override
    public IPerson setGender(Gender gender) {
        patient.setGender(FhirUtilR4.convertEnum(gender, Enumerations.AdministrativeGender.class, Enumerations.AdministrativeGender.OTHER));
        return this;
    }

    @Override
    public Date getBirthDate() {
        return patient.getBirthDate();
    }

    @Override
    public IPerson setBirthDate(Date date) {
        patient.setBirthDate(date);
        return this;
    }

    @Override
    public Date getDeceasedDate() {
        DateTimeType deceased = patient.hasDeceasedDateTimeType() ? patient.getDeceasedDateTimeType() : null;
        return deceased == null ? null : deceased.getValue();
    }

    @Override
    public IPerson setDeceasedDate(Date date) {
        patient.setDeceased(new DateTimeType(date));
        return this;
    }

    @Override
    public List<IPersonName> getNames() {
        return names;
    }

    @Override
    public List<IPostalAddress> getAddresses() {
        return null;
    }

    @Override
    public List<IPersonPhoto> getPhotos() {
        return null;
    }

    @Override
    public String getId() {
        return patient.getId();
    }

    @Override
    public Patient getWrapped() {
        return patient;
    }
}
