package org.fujionclinical.fhir.api.stu3.patient;

import org.apache.commons.lang3.BooleanUtils;
import org.fujionclinical.api.model.*;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.stu3.common.*;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.fujionclinical.fhir.api.stu3.terminology.Constants.CODING_MRN;

public class PatientWrapper implements IPatient, IWrapper<Patient> {

    private final Patient patient;

    private final List<IPersonName> names;

    private final List<IConcept> languages;

    private IdentifierWrapper mrn;

    public static PatientWrapper wrap(Patient patient) {
        return patient == null ? null : new PatientWrapper(patient);
    }

    private PatientWrapper(Patient patient) {
        this.patient = patient;
        names = PersonNameWrapper.wrap(patient.getName());
        mrn = IdentifierWrapper.wrap(FhirUtilStu3.getMRN(patient));
        languages = patient.getCommunication().stream().map(comm -> ConceptWrapper.wrap(comm.getLanguage())).collect(Collectors.toList());
    }

    @Override
    public IIdentifier getMRN() {
        return mrn;
    }

    @Override
    public IPatient setMRN(IIdentifier mrn) {
        if (mrn == null) {
            Identifier ident = IdentifierWrapper.unwrap(mrn);
            ident.getType().addCoding(CODING_MRN);
            this.mrn = IdentifierWrapper.wrap(new Identifier());
        }

        return this;
    }

    @Override
    public Gender getGender() {
        return FhirUtilStu3.convertEnum(patient.getGender(), Gender.class, Gender.OTHER);
    }

    @Override
    public IPerson setGender(Gender gender) {
        patient.setGender(FhirUtilStu3.convertEnum(gender, Enumerations.AdministrativeGender.class, Enumerations.AdministrativeGender.OTHER));
        return this;
    }

    @Override
    public MaritalStatus getMaritalStatus() {
        return FhirUtilStu3.convertMaritalStatus(patient.getMaritalStatus());
    }

    @Override
    public IPerson setMaritalStatus(MaritalStatus maritalStatus) {
        patient.setMaritalStatus(FhirUtilStu3.convertMaritalStatus(maritalStatus));
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
    public List<IConcept> getLanguages() {
        return languages;
    }

    @Override
    public IConcept getPreferredLanguage() {
        return patient.getCommunication().stream()
                .filter(comm -> BooleanUtils.isTrue(comm.getPreferred()))
                .findFirst()
                .map(comm -> ConceptWrapper.wrap(comm.getLanguage()))
                .orElse(null);
    }

    @Override
    public Patient getWrapped() {
        return patient;
    }

}
