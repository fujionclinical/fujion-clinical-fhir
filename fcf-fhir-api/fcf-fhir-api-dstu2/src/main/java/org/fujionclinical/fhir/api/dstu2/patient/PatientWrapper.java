package org.fujionclinical.fhir.api.dstu2.patient;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import org.apache.commons.lang3.BooleanUtils;
import org.fujionclinical.api.model.*;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.dstu2.common.*;
import org.fujionclinical.fhir.api.dstu2.terminology.Constants;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        mrn = IdentifierWrapper.wrap(FhirUtilDstu2.getMRN(patient));
        languages = patient.getCommunication().stream().map(comm -> ConceptWrapper.wrap(comm.getLanguage())).collect(Collectors.toList());
    }

    @Override
    public IIdentifier getMRN() {
        return mrn;
    }

    @Override
    public IPatient setMRN(IIdentifier mrn) {
        if (mrn == null) {
            IdentifierDt ident = IdentifierWrapper.unwrap(mrn);
            ident.getType().addCoding(Constants.CODING_MRN);
            this.mrn = IdentifierWrapper.wrap(new IdentifierDt());
        }

        return this;
    }

    @Override
    public Gender getGender() {
        return FhirUtilDstu2.convertEnum(patient.getGender(), Gender.class, Gender.OTHER);
    }

    @Override
    public IPerson setGender(Gender gender) {
        patient.setGender(FhirUtilDstu2.convertEnum(gender, AdministrativeGenderEnum.class, AdministrativeGenderEnum.OTHER));
        return this;
    }

    @Override
    public MaritalStatus getMaritalStatus() {
        return FhirUtilDstu2.convertMaritalStatus(patient.getMaritalStatus().getValueAsEnum());
    }

    @Override
    public IPerson setMaritalStatus(MaritalStatus maritalStatus) {
        patient.setMaritalStatus(FhirUtilDstu2.convertMaritalStatus(maritalStatus));
        return this;
    }

    @Override
    public Date getBirthDate() {
        return patient.getBirthDate();
    }

    @Override
    public IPerson setBirthDate(Date date) {
        patient.setBirthDateWithDayPrecision(date);
        return this;
    }

    @Override
    public Date getDeceasedDate() {
        IDatatype deceased = patient.getDeceased();
        return deceased instanceof DateDt ? ((DateDt) deceased).getValue() : null;
    }

    @Override
    public IPerson setDeceasedDate(Date date) {
        patient.setDeceased(new DateDt(date));
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
        return patient.getId().getIdPart();
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
