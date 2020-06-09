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
