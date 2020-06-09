package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import org.fujionclinical.api.model.core.IContactPoint;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class ContactPointTransform implements IWrapperTransform<IContactPoint, ContactPointDt> {

    public static final ContactPointTransform instance = new ContactPointTransform();

    @Override
    public ContactPointDt _unwrap(IContactPoint value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContactPoint _wrap(ContactPointDt value) {
        return new ContactPointWrapper(value);
    }

}
