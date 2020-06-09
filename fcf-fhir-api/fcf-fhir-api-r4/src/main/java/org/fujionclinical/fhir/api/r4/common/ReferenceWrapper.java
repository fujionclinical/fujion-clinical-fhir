package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.core.IWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Reference;

public class ReferenceWrapper<T extends IBaseResource> implements IWrapper<T> {

    private final Reference reference;

    private final Class<T> resourceClass;

    public static <T extends IBaseResource> ReferenceWrapper wrap(
            Class<T> resourceClass,
            Reference reference) {
        return reference == null ? null : new ReferenceWrapper(resourceClass, reference);
    }

    private ReferenceWrapper(
            Class<T> resourceClass,
            Reference reference) {
        this.resourceClass = resourceClass;
        this.reference = reference;
    }

    @Override
    public T getWrapped() {
        return FhirUtilR4.getFhirService().getResource(reference, resourceClass);
    }

    public void setResource(T resource) {
        reference.setResource(resource);
        reference.setReferenceElement(resource == null ? null : resource.getIdElement());
    }

}
