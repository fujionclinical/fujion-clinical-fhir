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

import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IIdentifier;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r4.model.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class IdentifierWrapper implements IIdentifier, IWrapper<Identifier> {

    private final Identifier identifier;

    private final IConcept type;

    public static IdentifierWrapper wrap(Identifier identifier) {
        return identifier == null ? null : new IdentifierWrapper(identifier);
    }

    public static List<IIdentifier> wrap(List<Identifier> identifiers) {
        return identifiers == null ? null : identifiers.stream().map(identifier -> IdentifierWrapper.wrap(identifier)).collect(Collectors.toList());
    }

    public static Identifier unwrap(IIdentifier identifier) {
        if (identifier == null) {
            return null;
        }

        Identifier result = new Identifier()
                .setSystem(identifier.getSystem())
                .setValue(identifier.getValue());
        result.setType(ConceptWrapper.unwrap(identifier.getType()));
        return result;
    }

    private IdentifierWrapper(Identifier identifer) {
        this.identifier = identifer;
        type = ConceptWrapper.wrap(identifer.getType());
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
        return FhirUtilR4.convertEnum(identifier.getUse(), IdentifierUse.class);
    }

    @Override
    public void setUse(IdentifierUse use) {
        identifier.setUse(FhirUtilR4.convertEnum(use, Identifier.IdentifierUse.class));
    }

    @Override
    public Identifier getWrapped() {
        return identifier;
    }

}
