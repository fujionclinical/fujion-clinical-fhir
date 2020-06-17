package org.fujionclinical.fhir.api.dstu2.transform;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IDomainType;
import org.fujionclinical.api.model.core.IModelTransform;
import org.fujionclinical.api.model.core.IReference;
import org.fujionclinical.api.model.core.ModelTransformRegistry;
import org.fujionclinical.api.model.impl.Reference;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class ReferenceTransform<T extends IDomainType> extends AbstractDatatypeTransform<IReference<T>, ResourceReferenceDt> {

    private static final ReferenceTransform instance = new ReferenceTransform();

    public static ReferenceTransform getInstance() {
        return instance;
    }

    private ReferenceTransform() {
        super(CoreUtil.cast(IReference.class), ResourceReferenceDt.class);
    }

    @Override
    public ResourceReferenceDt fromLogicalModel(IReference<T> src) {
        return super.fromLogicalModel(src);
    }

    @Override
    public ResourceReferenceDt _fromLogicalModel(IReference<T> src) {
        ResourceReferenceDt dest = new ResourceReferenceDt();
        IModelTransform transform = ModelTransformRegistry.getInstance().get(IBaseResource.class, src.getDomainType());
        dest.setResource((IBaseResource) transform.fromLogicalModel(src.hasReferenced() ? src.getReferenced() : null));
        String resourceName = FhirUtil.getResourceName(transform.getNativeType());
        dest.setReference(resourceName + "/" + src.getId());
        return dest;
    }

    @Override
    public IReference<T> toLogicalModel(ResourceReferenceDt src) {
        return super.toLogicalModel(src);
    }

    @Override
    public IReference<T> _toLogicalModel(ResourceReferenceDt src) {
        Class<?> resourceClass = FhirUtil.getResourceType(src.getReference());
        IModelTransform transform = ModelTransformRegistry.getInstance().get(IDomainType.class, resourceClass);
        IDomainType referenced = (T) transform.toLogicalModel(src.getResource());
        IReference<T> dest = referenced != null ? new Reference(referenced) : new Reference<T>(transform.getLogicalType(), src.getElementSpecificId());
        return dest;
    }

}
