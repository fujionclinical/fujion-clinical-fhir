/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import org.fujionclinical.api.model.core.*;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

public class ReferenceWrapper<L extends IDomainType> implements IWrapper<L> {

    private static class ReferenceTransform<L extends IDomainType> implements IWrapperTransform<L, ResourceReferenceDt> {

        @Override
        public L _wrap(ResourceReferenceDt value) {
            return new ReferenceWrapper<L>(value).getWrapped();
        }

        @Override
        public ResourceReferenceDt _unwrap(L value) {
            IWrapperTransform transform = WrapperTransformRegistry.getInstance().get(value.getClass(), IBaseResource.class);
            IBaseResource resource = (IBaseResource) transform.unwrap(value);
            ResourceReferenceDt ref = newWrapped();
            ref.setResource(resource);
            return ref;
        }

        @Override
        public ResourceReferenceDt newWrapped() {
            return new ResourceReferenceDt();
        }

    }

    private static final ReferenceTransform transform = new ReferenceTransform();

    private ResourceReferenceDt reference;

    public static <L extends IDomainType> ReferenceWrapper<L> wrap(
            ResourceReferenceDt reference) {
        return reference == null ? null : new ReferenceWrapper(reference);
    }

    public static <L extends IDomainType> List<L> wrap(List<ResourceReferenceDt> reference) {
        return reference == null ? null : new WrappedList(reference, transform);
    }

    private ReferenceWrapper(ResourceReferenceDt reference) {
        this.reference = reference;
    }

    @Override
    public L getWrapped() {
        IBaseResource resource = getResource();

        if (resource == null) {
            return null;
        }

        IWrapperTransform transform = WrapperTransformRegistry.getInstance().get(IDomainType.class, resource.getClass());
        return (L) transform.wrap(resource);
    }

    public void setWrapped(L resource) {
        reference = transform._unwrap(resource);
    }

    public IBaseResource getResource() {
        return FhirUtilDstu2.getFhirService().getResource(reference);
    }

    public void setResource(IBaseResource resource) {
        reference.setResource(resource);
        reference.setReference(resource == null ? null : resource.getIdElement());
    }

}
