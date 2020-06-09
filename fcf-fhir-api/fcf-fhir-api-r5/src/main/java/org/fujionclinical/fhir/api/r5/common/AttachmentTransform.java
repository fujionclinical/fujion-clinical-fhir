package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IAttachment;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r5.model.Attachment;

public class AttachmentTransform implements IWrapperTransform<IAttachment, Attachment> {

    public static final AttachmentTransform instance = new AttachmentTransform();

    @Override
    public IAttachment _wrap(Attachment value) {
        return new AttachmentWrapper(value);
    }

    @Override
    public Attachment newWrapped() {
        return new Attachment();
    }

}
