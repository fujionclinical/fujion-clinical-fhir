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
package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptCodeWrapper implements IConceptCode, IWrapper<Coding> {

    public static ConceptCodeWrapper wrap(Coding coding) {
        return coding == null ? null : new ConceptCodeWrapper(coding);
    }

    public static List<IConceptCode> wrap(CodeableConcept codeableConcept) {
        return codeableConcept == null ? Collections.emptyList() : wrap(codeableConcept.getCoding());
    }

    public static List<IConceptCode> wrap(List<Coding> codings) {
        return codings == null ? Collections.emptyList() : codings.stream().map(coding -> ConceptCodeWrapper.wrap(coding)).collect(Collectors.toList());
    }

    public static Coding unwrap(IConceptCode code) {
        return code == null ? null : new Coding()
                .setSystem(code.getSystem())
                .setCode(code.getCode())
                .setDisplay(code.getText());
    }

    public static List<Coding> unwrap(List<IConceptCode> codes) {
        return codes == null ? null : codes.stream().map(code -> unwrap(code)).collect(Collectors.toList());
    }

    private final Coding coding;

    private ConceptCodeWrapper(Coding coding) {
        this.coding = coding;
    }

    @Override
    public String getSystem() {
        return coding.getSystem();
    }

    @Override
    public void setSystem(String system) {
        coding.setSystem(system);
    }

    @Override
    public String getCode() {
        return coding.getCode();
    }

    @Override
    public void setCode(String code) {
        coding.setCode(code);
    }

    @Override
    public String getText() {
        return coding.getDisplay();
    }

    @Override
    public void setText(String text) {
        coding.setDisplay(text);
    }

    @Override
    public Coding getWrapped() {
        return coding;
    }
}