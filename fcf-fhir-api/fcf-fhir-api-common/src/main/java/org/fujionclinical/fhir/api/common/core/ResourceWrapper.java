package org.fujionclinical.fhir.api.common.core;

import org.apache.commons.beanutils.ConstructorUtils;
import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IDomainObject;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.BeanUtils;

import java.util.function.Function;

public class ResourceWrapper<T extends IBaseResource> implements IDomainObject, IWrapper<T> {

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

    @Override
    public T getWrapped() {
        return resource;
    }
}
