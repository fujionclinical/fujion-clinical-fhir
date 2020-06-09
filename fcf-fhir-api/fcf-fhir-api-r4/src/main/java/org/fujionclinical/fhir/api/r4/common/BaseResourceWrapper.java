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

import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.fhir.api.common.core.AbstractResourceWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Identifier;

import java.util.List;

public abstract class BaseResourceWrapper<T extends IBaseResource> extends AbstractResourceWrapper<T> {

    private final List<IIdentifier> identifiers;

    protected BaseResourceWrapper(T resource) {
        super(resource);
        identifiers = IdentifierTransform.instance.wrap(_getIdentifiers());
    }

    protected abstract List<Identifier> _getIdentifiers();

    @Override
    public final List<IIdentifier> getIdentifiers() {
        return identifiers;
    }
}
