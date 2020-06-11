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
package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.core.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Reference;

import java.util.List;

public class ReferenceWrapper<L extends IDomainObject> implements IWrapper<L> {

    private static class ReferenceTransform<L extends IDomainObject> implements IWrapperTransform<L, Reference> {

        @Override
        public L _wrap(Reference value) {
            return new ReferenceWrapper<L>(value).getWrapped();
        }

        @Override
        public Reference _unwrap(L value) {
            IWrapperTransform transform = WrapperTransformRegistry.getInstance().get(value.getClass(), IBaseResource.class);
            IBaseResource resource = (IBaseResource) transform.unwrap(value);
            Reference ref = newWrapped();
            ref.setResource(resource);
            return ref;
        }

        @Override
        public Reference newWrapped() {
            return new Reference();
        }

    }

    private static final ReferenceTransform transform = new ReferenceTransform();

    private Reference reference;

    public static <L extends IDomainObject> ReferenceWrapper<L> wrap(
            Reference reference) {
        return reference == null ? null : new ReferenceWrapper(reference);
    }

    public static <L extends IDomainObject> List<L> wrap(List<Reference> reference) {
        return reference == null ? null : new WrappedList(reference, transform);
    }

    private ReferenceWrapper(Reference reference) {
        this.reference = reference;
    }

    @Override
    public L getWrapped() {
        IBaseResource resource = getResource();

        if (resource == null) {
            return null;
        }

        IWrapperTransform transform = WrapperTransformRegistry.getInstance().get(IDomainObject.class, resource.getClass());
        return (L) transform.wrap(resource);
    }

    public void setWrapped(L resource) {
        reference = transform._unwrap(resource);
    }

    public IBaseResource getResource() {
        return FhirUtilR4.getFhirService().getResource(reference);
    }

    public void setResource(IBaseResource resource) {
        reference.setResource(resource);
        reference.setReference(resource == null ? null : resource.getIdElement().getValue());
    }

}
