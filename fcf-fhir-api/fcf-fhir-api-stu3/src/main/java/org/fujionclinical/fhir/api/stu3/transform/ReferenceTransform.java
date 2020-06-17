package org.fujionclinical.fhir.api.stu3.transform;

import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IDomainType;
import org.fujionclinical.api.model.core.IModelTransform;
import org.fujionclinical.api.model.core.IReference;
import org.fujionclinical.api.model.core.ModelTransformRegistry;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class ReferenceTransform<T extends IDomainType> extends AbstractDatatypeTransform<IReference<T>, Reference> {

    private static final ReferenceTransform instance = new ReferenceTransform();

    public static ReferenceTransform getInstance() {
        return instance;
    }

    private ReferenceTransform() {
        super(CoreUtil.cast(IReference.class), Reference.class);
    }

    @Override
    public Reference fromLogicalModel(IReference<T> src) {
        return super.fromLogicalModel(src);
    }

    @Override
    public Reference _fromLogicalModel(IReference<T> src) {
        Reference dest = new Reference();
        IModelTransform transform = ModelTransformRegistry.getInstance().get(IBaseResource.class, src.getDomainType());
        dest.setResource((IBaseResource) transform.fromLogicalModel(src.hasReferenced() ? src.getReferenced() : null));
        String resourceName = FhirUtil.getResourceName(transform.getNativeType());
        dest.setReference(resourceName + "/" + src.getId());
        return dest;
    }

    @Override
    public IReference<T> toLogicalModel(Reference src) {
        return super.toLogicalModel(src);
    }

    @Override
    public IReference<T> _toLogicalModel(Reference src) {
        Class<?> resourceClass = FhirUtil.getResourceType(src.getReference());
        IModelTransform transform = ModelTransformRegistry.getInstance().get(resourceClass, IDomainType.class);
        IDomainType referenced = (T) transform.toLogicalModel(src.getResource());
        IReference<T> dest = referenced != null
                ? new org.fujionclinical.api.model.impl.Reference(referenced)
                : new org.fujionclinical.api.model.impl.Reference<T>(transform.getLogicalType(), src.getId());
        return dest;
    }

}
