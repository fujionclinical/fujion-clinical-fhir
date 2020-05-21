package org.fujionclinical.fhir.api.r4.patient;

import org.fujionclinical.api.model.Address;
import org.fujionclinical.api.model.Identifier;
import org.fujionclinical.api.model.PersonName;
import org.fujionclinical.api.model.PersonPhoto;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.r4.common.ConversionUtil;
import org.fujionclinical.fhir.api.r4.common.FhirUtil;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Patient;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PatientWrapper extends ResourceWrapper<Patient> implements IPatient {

    public PatientWrapper(Patient resource) {
        super(resource);
    }

    @Override
    public Identifier getMRN() {
        return ConversionUtil.identifier(FhirUtil.getMRN(getNative()));
    }

    @Override
    public String getGender() {
        return getNative().getGender().toCode();
    }

    @Override
    public Date getDOB() {
        return getNative().getBirthDate();
    }

    @Override
    public Date getDeceased() {
        return getNative().hasDeceasedDateTimeType() ? getNative().getDeceasedDateTimeType().getValue() : null;
    }

    @Override
    public List<PersonName> getNames() {
        return getNative().getName().stream().map(name -> ConversionUtil.personName(name)).collect(Collectors.toList());
    }

    @Override
    public List<Address> getAddresses() {
        List<org.hl7.fhir.r4.model.Address> addresses = getNative().getAddress();

        return addresses == null || addresses.isEmpty() ? null :
                addresses.stream()
                        .map(address -> ConversionUtil.address(address))
                        .collect(Collectors.toList());
    }

    @Override
    public List<PersonPhoto> getPhotos() {
        List<Attachment> attachments = getNative().getPhoto();
        return attachments == null || attachments.isEmpty() ? null :
                attachments.stream()
                        .filter(attachment -> attachment.getContentType().startsWith("image/"))
                        .map(attachment -> FhirUtil.getContent(attachment))
                        .map(content -> new PersonPhoto(content, PersonPhoto.PersonPhotoCategory.USUAL))
                        .collect(Collectors.toList());
    }

}
