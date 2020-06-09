package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.fhir.api.common.core.AbstractResourceWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Identifier;

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
