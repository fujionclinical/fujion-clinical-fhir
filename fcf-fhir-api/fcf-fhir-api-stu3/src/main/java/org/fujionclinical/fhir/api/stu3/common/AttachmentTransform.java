package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IAttachment;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Attachment;

public class AttachmentTransform implements IWrapperTransform<IAttachment, Attachment> {

    public static final AttachmentTransform instance = new AttachmentTransform();

    @Override
    public Attachment _unwrap(IAttachment value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAttachment _wrap(Attachment value) {
        return new AttachmentWrapper(value);
    }

}
