package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import org.fujion.ancillary.MimeContent;
import org.fujionclinical.api.model.IPersonPhoto;
import org.fujionclinical.api.model.IWrapper;

public class PersonPhotoWrapper implements IPersonPhoto, IWrapper<AttachmentDt> {

    public static PersonPhotoWrapper create(AttachmentDt attachment) {
        return attachment == null ? null : new PersonPhotoWrapper(attachment);
    }

    private final AttachmentDt attachment;

    private PersonPhotoWrapper(AttachmentDt attachment) {
        this.attachment = attachment;
    }

    @Override
    public PersonPhotoCategory getCategory() {
        return null;
    }

    @Override
    public IPersonPhoto setCategory(PersonPhotoCategory category) {
        return null;
    }

    @Override
    public MimeContent getImage() {
        return null;
    }

    @Override
    public IPersonPhoto setImage(MimeContent content) {
        return null;
    }

    @Override
    public AttachmentDt getWrapped() {
        return attachment;
    }
}
