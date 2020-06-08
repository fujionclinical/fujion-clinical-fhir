package org.fujionclinical.fhir.api.r5.practitioner;

import org.fujionclinical.api.model.core.*;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.practitioner.IPractitioner;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.AbstractResourceWrapper;
import org.fujionclinical.fhir.api.r5.common.*;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Practitioner;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PractitionerWrapper extends AbstractResourceWrapper<Practitioner> implements IPractitioner {

    private final List<IPersonName> names;

    private final List<IConcept> languages;

    public static PractitionerWrapper wrap(Practitioner practitioner) {
        return practitioner == null ? null : new PractitionerWrapper(practitioner);
    }

    public static Practitioner unwrap(IPractitioner practitioner) {
        if (practitioner == null) {
            return null;
        }

        if (practitioner instanceof PractitionerWrapper) {
            return ((PractitionerWrapper) practitioner).getWrapped();
        }

        PractitionerWrapper pract = wrap(new Practitioner());
        BeanUtils.copyProperties(practitioner, pract);
        return pract.getWrapped();
    }

    private PractitionerWrapper(Practitioner practitioner) {
        super(practitioner);
        names = PersonNameWrapper.wrap(practitioner.getName());
        languages = practitioner.getCommunication().stream().map(lang -> ConceptWrapper.wrap(lang)).collect(Collectors.toList());
    }

    @Override
    public List<IIdentifier> getIdentifiers() {
        return getWrapped().getIdentifier().stream().map(identifier -> IdentifierWrapper.wrap(identifier)).collect(Collectors.toList());
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
        getWrapped().setGender(FhirUtil.convertEnum(gender, Enumerations.AdministrativeGender.class, Enumerations.AdministrativeGender.OTHER));
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
        getWrapped().setBirthDate(date);
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
        return getWrapped().getPhoto().stream().map(AttachmentWrapper::wrap).collect(Collectors.toList());
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return getWrapped().getTelecom().stream().map(ContactPointWrapper::wrap).collect(Collectors.toList());
    }

    @Override
    public List<IPostalAddress> getAddresses() {
        return getWrapped().getAddress().stream().map(PostalAddressWrapper::wrap).collect(Collectors.toList());
    }

}
