package org.fujionclinical.fhir.api.r5.patient;

import org.fujionclinical.api.model.IAttachment;
import org.fujionclinical.api.model.IIdentifier;
import org.fujionclinical.api.model.IPostalAddress;
import org.fujionclinical.api.model.IWrapper;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.r5.common.*;
import org.fujionclinical.fhir.api.r5.terminology.Constants;
import org.hl7.fhir.r5.model.DateTimeType;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.Patient;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PatientWrapper implements IPatient, IWrapper<Patient> {

    public static PatientWrapper wrap(Patient patient) {
        return patient == null ? null : new PatientWrapper(patient);
    }

    private final Patient patient;

    private final List<IPersonName> names;

    private IdentifierWrapper mrn;

    private PatientWrapper(Patient patient) {
        this.patient = patient;
        names = PersonNameWrapper.wrap(patient.getName());
        mrn = IdentifierWrapper.wrap(FhirUtilR5.getMRN(patient));
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
            this.mrn = IdentifierWrapper.wrap(new Identifier());
        }

        return this;
    }

    @Override
    public Gender getGender() {
        return FhirUtilR5.convertEnum(patient.getGender(), Gender.class, Gender.OTHER);
    }

    @Override
    public IPerson setGender(Gender gender) {
        patient.setGender(FhirUtilR5.convertEnum(gender, Enumerations.AdministrativeGender.class, Enumerations.AdministrativeGender.OTHER));
        return this;
    }

    @Override
    public MaritalStatus getMaritalStatus() {
        return FhirUtilR5.convertMaritalStatus(patient.getMaritalStatus());
    }

    @Override
    public IPerson setMaritalStatus(MaritalStatus maritalStatus) {
        patient.setMaritalStatus(FhirUtilR5.convertMaritalStatus(maritalStatus));
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
        return patient.getAddress().stream().map(address -> PostalAddressWrapper.wrap(address)).collect(Collectors.toList());
    }

    @Override
    public List<IAttachment> getPhotos() {
        return patient.getPhoto().stream().map(photo -> AttachmentWrapper.wrap(photo)).collect(Collectors.toList());
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
