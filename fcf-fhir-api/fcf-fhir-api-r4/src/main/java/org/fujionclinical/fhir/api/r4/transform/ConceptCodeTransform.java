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
package org.fujionclinical.fhir.api.r4.transform;

import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.impl.ConceptCode;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r4.model.Coding;

public class ConceptCodeTransform extends AbstractDatatypeTransform<IConceptCode, Coding> {

    private static final ConceptCodeTransform instance = new ConceptCodeTransform();

    public static ConceptCodeTransform getInstance() {
        return instance;
    }

    private ConceptCodeTransform() {
        super(IConceptCode.class, Coding.class);
    }

    @Override
    public Coding _fromLogicalModel(IConceptCode src) {
        return new Coding(src.getSystem(), src.getCode(), src.getText());
    }

    @Override
    public IConceptCode _toLogicalModel(Coding src) {
        return new ConceptCode(src.getSystem(), src.getCode(), src.getDisplay());
    }

}
