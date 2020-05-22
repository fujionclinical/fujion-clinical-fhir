package org.fujionclinical.fhir.api.dstu2.patient;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import org.fujionclinical.api.model.*;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtil;
import org.fujionclinical.fhir.api.dstu2.common.IdentifierWrapper;
import org.fujionclinical.fhir.api.dstu2.common.PersonNameWrapper;
import org.fujionclinical.fhir.api.dstu2.terminology.Constants;

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
        mrn = IdentifierWrapper.create(FhirUtil.getMRN(patient));
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
            this.mrn = IdentifierWrapper.create(new IdentifierDt());
        }

        return this;
    }

    @Override
    public Gender getGender() {
        return FhirUtil.convertEnum(patient.getGender(), Gender.class, Gender.OTHER);
    }

    @Override
    public IPerson setGender(Gender gender) {
        patient.setGender(FhirUtil.convertEnum(gender, AdministrativeGenderEnum.class, AdministrativeGenderEnum.OTHER));
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
        return null;
    }

    @Override
    public List<IPersonPhoto> getPhotos() {
        return null;
    }

    @Override
    public String getId() {
        return patient.getId().getIdPart();
    }

    @Override
    public Patient getWrapped() {
        return patient;
    }
}
