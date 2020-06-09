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

import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.api.model.core.IWrapper;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.r4.model.Identifier;

public class IdentifierWrapper implements IIdentifier, IWrapper<Identifier> {

    private final Identifier identifier;

    private final IConcept type;

    protected IdentifierWrapper(Identifier identifer) {
        this.identifier = identifer;
        type = ConceptTransform.instance.wrap(identifer.getType());
    }

    @Override
    public String getSystem() {
        return identifier.getSystem();
    }

    @Override
    public void setSystem(String system) {
        identifier.setSystem(system);
    }

    @Override
    public String getValue() {
        return identifier.getValue();
    }

    @Override
    public void setValue(String value) {
        identifier.setValue(value);
    }

    @Override
    public IConcept getType() {
        return type;
    }

    @Override
    public IdentifierUse getUse() {
        return FhirUtil.convertEnum(identifier.getUse(), IdentifierUse.class);
    }

    @Override
    public void setUse(IdentifierUse use) {
        identifier.setUse(FhirUtil.convertEnum(use, Identifier.IdentifierUse.class));
    }

    @Override
    public Identifier getWrapped() {
        return identifier;
    }

}
