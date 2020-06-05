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

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IWrapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptWrapper implements IConcept, IWrapper<CodeableConceptDt> {

    private final CodeableConceptDt codeableConcept;

    private final List<IConceptCode> codes;

    public static ConceptWrapper wrap(CodeableConceptDt codeableConcept) {
        return codeableConcept == null ? null : new ConceptWrapper(codeableConcept);
    }

    public static List<IConcept> wrap(List<CodeableConceptDt> concepts) {
        return concepts == null ? Collections.emptyList() : concepts.stream().map(concept -> ConceptWrapper.wrap(concept)).collect(Collectors.toList());
    }

    public static CodeableConceptDt unwrap(IConcept concept) {
        return concept == null ? null : new CodeableConceptDt()
                .setText(concept.getText())
                .setCoding(ConceptCodeWrapper.unwrap(concept.getCodes()));
    }

    private ConceptWrapper(CodeableConceptDt codeableConcept) {
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
    public CodeableConceptDt getWrapped() {
        return codeableConcept;
    }

}
