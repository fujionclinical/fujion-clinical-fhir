package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class IdentifierTransform implements IWrapperTransform<IIdentifier, IdentifierDt> {

    public static final IdentifierTransform instance = new IdentifierTransform();

    @Override
    public IdentifierDt _unwrap(IIdentifier identifier) {
        IdentifierDt result = new IdentifierDt()
                .setSystem(identifier.getSystem())
                .setValue(identifier.getValue());
        result.getType().setText(identifier.getType().getText()).setCoding(ConceptCodeTransform.instance.unwrap(identifier.getType().getCodes()));
        return result;
    }

    @Override
    public IIdentifier _wrap(IdentifierDt identifier) {
        return new IdentifierWrapper(identifier);
    }

}
