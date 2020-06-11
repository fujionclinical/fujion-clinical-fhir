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

import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IDomainObject;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

public abstract class AbstractResourceWrapper<T extends IBaseResource> extends AbstractWrapper<T> implements IDomainObject {

    private final List<IConceptCode> tags;

    protected AbstractResourceWrapper(T resource) {
        super(resource);
        this.tags = TagTransform.getInstance().wrap((List<IBaseCoding>) resource.getMeta().getTag());
    }

    @Override
    public String getId() {
        return getWrapped().getIdElement().getIdPart();
    }

    @Override
    public void setId(String id) {
        getWrapped().setId(id);
    }

    @Override
    public List<IConceptCode> getTags() {
        return tags;
    }

    @Override
    public T getNative() {
        return getWrapped();
    }

}
