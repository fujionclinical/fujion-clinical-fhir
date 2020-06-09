package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IWrapper;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IBaseResource;

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
        return FhirUtilStu3.getFhirService().getResource(reference, resourceClass);
    }

    public void setResource(T resource) {
        reference.setResource(resource);
        reference.setReferenceElement(resource == null ? null : resource.getIdElement());
    }

}
