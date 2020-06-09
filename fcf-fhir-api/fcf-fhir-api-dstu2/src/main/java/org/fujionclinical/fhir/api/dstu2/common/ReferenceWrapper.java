package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import org.fujionclinical.api.model.core.IWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class ReferenceWrapper<T extends IBaseResource> implements IWrapper<T> {

    private final ResourceReferenceDt reference;

    private final Class<T> resourceClass;

    public static <T extends IBaseResource> ReferenceWrapper wrap(Class<T> resourceClass, ResourceReferenceDt reference) {
        return reference == null ? null : new ReferenceWrapper(resourceClass, reference);
    }

    private ReferenceWrapper(
            Class<T> resourceClass,
            ResourceReferenceDt reference) {
        this.resourceClass = resourceClass;
        this.reference = reference;
    }

    @Override
    public T getWrapped() {
        return FhirUtilDstu2.getFhirService().getResource(reference, resourceClass);
    }

    public void setResource(T resource) {
        reference.setResource(resource);
        reference.setReference(resource == null ? null : resource.getIdElement());
    }

}
