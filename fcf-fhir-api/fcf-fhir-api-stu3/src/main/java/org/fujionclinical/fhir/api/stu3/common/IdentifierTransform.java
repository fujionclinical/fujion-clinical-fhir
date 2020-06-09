package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Identifier;

public class IdentifierTransform implements IWrapperTransform<IIdentifier, Identifier> {

    public static final IdentifierTransform instance = new IdentifierTransform();

    @Override
    public Identifier _unwrap(IIdentifier identifier) {
        Identifier result = new Identifier()
                .setSystem(identifier.getSystem())
                .setValue(identifier.getValue());
        result.getType().setText(identifier.getType().getText()).setCoding(ConceptCodeTransform.instance.unwrap(identifier.getType().getCodes()));
        return result;
    }

    @Override
    public IIdentifier _wrap(Identifier identifier) {
        return new IdentifierWrapper(identifier);
    }

}
