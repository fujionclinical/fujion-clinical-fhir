package org.fujionclinical.fhir.api.dstu2.patient;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateDt;
import org.fujionclinical.api.model.Identifier;
import org.fujionclinical.api.model.PersonName;
import org.fujionclinical.api.model.PersonPhoto;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.dstu2.common.ConversionUtil;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtil;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PatientWrapper extends ResourceWrapper<Patient> implements IPatient {

    public PatientWrapper(Patient resource) {
        super(resource);
    }

    @Override
    public Identifier getMRN() {
        return ConversionUtil.identifier((FhirUtil.getMRN(getNative())));
    }

    @Override
    public String getGender() {
        return getNative().getGender();
    }

    @Override
    public Date getDOB() {
        return getNative().getBirthDate();
    }

    @Override
    public Date getDeceased() {
        IDatatype value = getNative().getDeceased();
        return value instanceof DateDt ? ((DateDt) value).getValue() : null;
    }

    @Override
    public List<PersonName> getNames() {
        return getNative().getName().stream().map(name -> ConversionUtil.personName(name)).collect(Collectors.toList());
    }

    @Override
    public List<PersonPhoto> getPhotos() {
        List<AttachmentDt> attachments = getNative().getPhoto();
        return attachments == null || attachments.isEmpty() ? null :
                attachments.stream()
                        .filter(attachment -> attachment.getContentType().startsWith("image/"))
                        .map(attachment -> FhirUtil.getContent(attachment))
                        .map(content -> new PersonPhoto(content, PersonPhoto.PersonPhotoCategory.USUAL))
                        .collect(Collectors.toList());
    }
}
