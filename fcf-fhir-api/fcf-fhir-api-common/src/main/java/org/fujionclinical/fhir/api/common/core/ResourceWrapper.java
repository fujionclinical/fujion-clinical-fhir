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
package org.fujionclinical.fhir.api.common.core;

import org.fujionclinical.api.model.core.IDomainObject;
import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.api.model.core.IWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;

import java.util.List;
import java.util.stream.Collectors;

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
