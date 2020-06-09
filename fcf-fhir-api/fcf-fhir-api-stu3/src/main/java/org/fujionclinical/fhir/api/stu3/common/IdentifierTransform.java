package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Identifier;

public class IdentifierTransform implements IWrapperTransform<IIdentifier, Identifier> {

    public static final IdentifierTransform instance = new IdentifierTransform();

    @Override
    public IIdentifier _wrap(Identifier identifier) {
        return new IdentifierWrapper(identifier);
    }

    @Override
    public Identifier newWrapped() {
        return new Identifier();
    }

}
