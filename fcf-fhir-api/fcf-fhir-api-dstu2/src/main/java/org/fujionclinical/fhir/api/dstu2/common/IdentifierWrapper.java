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

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IIdentifier;
import org.fujionclinical.api.model.core.IWrapper;

import java.util.List;
import java.util.stream.Collectors;

public class IdentifierWrapper implements IIdentifier, IWrapper<IdentifierDt> {

    private final IdentifierDt identifier;

    private final IConcept type;

    public static IdentifierWrapper wrap(IdentifierDt identifier) {
        return identifier == null ? null : new IdentifierWrapper(identifier);
    }

    public static List<IIdentifier> wrap(List<IdentifierDt> identifiers) {
        return identifiers == null ? null : identifiers.stream().map(identifier -> IdentifierWrapper.wrap(identifier)).collect(Collectors.toList());
    }

    public static IdentifierDt unwrap(IIdentifier identifier) {
        if (identifier == null) {
            return null;
        }

        IdentifierDt result = new IdentifierDt()
                .setSystem(identifier.getSystem())
                .setValue(identifier.getValue());
        result.getType().setText(identifier.getType().getText()).setCoding(ConceptCodeWrapper.unwrap(identifier.getType().getCodes()));
        return result;
    }

    private IdentifierWrapper(IdentifierDt identifer) {
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
        return FhirUtilDstu2.convertEnum(identifier.getUseElement().getValueAsEnum(), IdentifierUse.class);
    }

    @Override
    public void setUse(IdentifierUse use) {
        identifier.setUse(FhirUtilDstu2.convertEnum(use, IdentifierUseEnum.class));
    }

    @Override
    public IdentifierDt getWrapped() {
        return identifier;
    }

}
