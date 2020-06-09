package org.fujionclinical.fhir.api.dstu2.practitioner;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import org.fujionclinical.api.model.core.*;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.practitioner.IPractitioner;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.dstu2.common.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PractitionerWrapper extends BaseResourceWrapper<Practitioner> implements IPractitioner {

    private final List<IPersonName> names;

    private final List<IConcept> languages;

    private final List<IContactPoint> contactPoints;

    private final List<IPostalAddress> addresses;

    private final List<IAttachment> photos;

    protected PractitionerWrapper(Practitioner practitioner) {
        super(practitioner);
        names = PersonNameTransform.instance.wrap(Collections.singletonList(practitioner.getName()));
        languages = ConceptTransform.instance.wrap(practitioner.getCommunication());
        contactPoints = ContactPointTransform.instance.wrap(practitioner.getTelecom());
        addresses = PostalAddressTransform.instance.wrap(practitioner.getAddress());
        photos = AttachmentTransform.instance.wrap(practitioner.getPhoto());
    }

    @Override
    protected List<IdentifierDt> _getIdentifiers() {
        return getWrapped().getIdentifier();
    }

    @Override
    public List<IPersonName> getNames() {
        return names;
    }

    @Override
    public Gender getGender() {
        return FhirUtil.convertEnum(getWrapped().getGender(), Gender.class, Gender.OTHER);
    }

    @Override
    public void setGender(Gender gender) {
        getWrapped().setGender(FhirUtil.convertEnum(gender, AdministrativeGenderEnum.class, AdministrativeGenderEnum.OTHER));
    }

    @Override
    public ConceptCode getBirthSex() {
        return null;
    }

    @Override
    public ConceptCode getEthnicity() {
        return null;
    }

    @Override
    public MaritalStatus getMaritalStatus() {
        return null;
    }

    @Override
    public ConceptCode getRace() {
        return null;
    }

    @Override
    public Date getBirthDate() {
        return getWrapped().getBirthDate();
    }

    @Override
    public void setBirthDate(Date date) {
        getWrapped().setBirthDateWithDayPrecision(date);
    }

    @Override
    public Date getDeceasedDate() {
        return null;
    }

    @Override
    public List<IConcept> getLanguages() {
        return languages;
    }

    @Override
    public IConcept getPreferredLanguage() {
        return hasLanguage() ? getLanguages().get(0) : null;
    }

    @Override
    public List<IAttachment> getPhotos() {
        return photos;
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return contactPoints;
    }

    @Override
    public List<IPostalAddress> getAddresses() {
        return addresses;
    }

}
