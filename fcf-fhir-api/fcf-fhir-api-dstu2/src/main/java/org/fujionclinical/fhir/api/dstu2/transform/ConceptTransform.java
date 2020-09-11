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
package org.fujionclinical.fhir.api.dstu2.transform;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.impl.Concept;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class ConceptTransform extends AbstractDatatypeTransform<IConcept, CodeableConceptDt> {

    private static final ConceptTransform instance = new ConceptTransform();

    public static ConceptTransform getInstance() {
        return instance;
    }

    private ConceptTransform() {
        super(IConcept.class, CodeableConceptDt.class);
    }

    @Override
    public CodeableConceptDt _fromLogicalModel(IConcept src) {
        CodeableConceptDt dest = new CodeableConceptDt();
        dest.setText(src.getText());
        dest.setCoding(ConceptCodeTransform.getInstance().fromLogicalModel(src.getCodes()));
        return dest;
    }

    @Override
    public IConcept _toLogicalModel(CodeableConceptDt src) {
        IConcept dest = new Concept(src.getText());
        dest.setCodes(ConceptCodeTransform.getInstance().toLogicalModel(src.getCoding()));
        return dest;
    }

}