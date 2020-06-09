package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class IdentifierTransform implements IWrapperTransform<IIdentifier, IdentifierDt> {

    public static final IdentifierTransform instance = new IdentifierTransform();

    @Override
    public IIdentifier _wrap(IdentifierDt identifier) {
        return new IdentifierWrapper(identifier);
    }

    @Override
    public IdentifierDt newWrapped() {
        return new IdentifierDt();
    }

}
