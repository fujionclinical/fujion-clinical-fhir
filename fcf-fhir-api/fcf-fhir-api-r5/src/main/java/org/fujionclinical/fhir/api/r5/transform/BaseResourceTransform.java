package org.fujionclinical.fhir.api.r5.transform;

import org.fujionclinical.api.model.core.IDomainType;
import org.fujionclinical.fhir.api.common.transform.AbstractResourceTransform;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Identifier;

import java.util.List;

public abstract class BaseResourceTransform<L extends IDomainType, N extends IBaseResource> extends AbstractResourceTransform<L, N> {

    protected BaseResourceTransform(
            Class<L> logicalModelType,
            Class<N> nativeModelType) {
        super(logicalModelType, nativeModelType);
    }

    @Override
    public N _fromLogicalModel(L src) {
        N dest = super._fromLogicalModel(src);
        List<Identifier> identifiers = getIdentifiers(dest);
        src.getIdentifiers().forEach(identifier -> identifiers.add(IdentifierTransform.getInstance().fromLogicalModel(identifier)));
        return dest;
    }

    @Override
    public L _toLogicalModel(N src) {
        L dest = super._toLogicalModel(src);
        List<Identifier> identifiers = getIdentifiers(src);
        identifiers.forEach(identifier -> dest.addIdentifiers(IdentifierTransform.getInstance().toLogicalModel(identifier)));
        return dest;
    }

    protected abstract List<Identifier> getIdentifiers(N object);

}
