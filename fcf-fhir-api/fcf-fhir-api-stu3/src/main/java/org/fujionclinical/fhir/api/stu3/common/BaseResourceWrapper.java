package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.fhir.api.common.core.AbstractResourceWrapper;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

public abstract class BaseResourceWrapper<T extends IBaseResource> extends AbstractResourceWrapper<T> {

    private final List<IIdentifier> identifiers;

    protected BaseResourceWrapper(T resource) {
        super(resource);
        identifiers = IdentifierTransform.instance.wrap(_getIdentifiers());
    }

    protected abstract List<Identifier> _getIdentifiers();

    @Override
    public final List<IIdentifier> getIdentifiers() {
        return identifiers;
    }
}
