package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import org.fujionclinical.api.model.core.IAttachment;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class AttachmentTransform implements IWrapperTransform<IAttachment, AttachmentDt> {

    public static final AttachmentTransform instance = new AttachmentTransform();

    @Override
    public IAttachment _wrap(AttachmentDt value) {
        return new AttachmentWrapper(value);
    }

    @Override
    public AttachmentDt newWrapped() {
        return new AttachmentDt();
    }

}
