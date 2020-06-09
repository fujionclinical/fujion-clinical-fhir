package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IContactPoint;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r5.model.ContactPoint;

public class ContactPointTransform implements IWrapperTransform<IContactPoint, ContactPoint> {

    public static final ContactPointTransform instance = new ContactPointTransform();

    @Override
    public ContactPoint _unwrap(IContactPoint value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContactPoint _wrap(ContactPoint value) {
        return new ContactPointWrapper(value);
    }

}
