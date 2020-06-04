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
package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r5.model.CodeableConcept;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptWrapper implements IConcept, IWrapper<CodeableConcept> {

    private final CodeableConcept codeableConcept;

    private final List<IConceptCode> codes;

    public static ConceptWrapper wrap(CodeableConcept codeableConcept) {
        return codeableConcept == null ? null : new ConceptWrapper(codeableConcept);
    }

    public static List<IConcept> wrap(List<CodeableConcept> concepts) {
        return concepts == null ? Collections.emptyList() : concepts.stream().map(concept -> ConceptWrapper.wrap(concept)).collect(Collectors.toList());    
    }
    
    public static CodeableConcept unwrap(IConcept concept) {
        return concept == null ? null : new CodeableConcept()
                .setText(concept.getText())
                .setCoding(ConceptCodeWrapper.unwrap(concept.getCodes()));
    }

    private ConceptWrapper(CodeableConcept codeableConcept) {
        this.codeableConcept = codeableConcept;
        this.codes = ConceptCodeWrapper.wrap(codeableConcept.getCoding());
    }

    @Override
    public String getText() {
        return codeableConcept.getText();
    }

    @Override
    public void setText(String text) {
        codeableConcept.setText(text);
    }

    @Override
    public List<IConceptCode> getCodes() {
        return codes;
    }

    @Override
    public CodeableConcept getWrapped() {
        return codeableConcept;
    }

}