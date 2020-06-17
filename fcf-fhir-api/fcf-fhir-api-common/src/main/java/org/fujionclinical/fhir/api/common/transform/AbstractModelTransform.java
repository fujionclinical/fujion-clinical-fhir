package org.fujionclinical.fhir.api.common.transform;

import org.fujionclinical.api.model.core.IModelTransform;

public abstract class AbstractModelTransform<L, N> implements IModelTransform<L, N> {

    private final Class<L> logicalModelType;

    private final Class<N> nativeModelType;

    protected AbstractModelTransform(
            Class<L> logicalModelType,
            Class<N> nativeModelType) {
        this.logicalModelType = logicalModelType;
        this.nativeModelType = nativeModelType;
    }

    @Override
    public Class<L> getLogicalType() {
        return logicalModelType;
    }

    @Override
    public Class<N> getNativeType() {
        return nativeModelType;
    }

}
