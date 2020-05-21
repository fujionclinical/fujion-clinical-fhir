package org.fujionclinical.fhir.api.common.core;

import org.fujionclinical.api.model.IDomainObject;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class ResourceWrapper<T extends IBaseResource> implements IDomainObject {

    private final T resource;

    public ResourceWrapper(T resource) {
        this.resource = resource;
    }

    @Override
    public String getId() {
        return resource.getIdElement().getIdPart();
    }

    public void setId(String id) {
        resource.setId(id);
    }

    @Override
    public T getNative() {
        return resource;
    }
}
