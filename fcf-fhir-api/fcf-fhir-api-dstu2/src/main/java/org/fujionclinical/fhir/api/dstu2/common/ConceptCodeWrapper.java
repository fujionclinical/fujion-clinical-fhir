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
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.fhir.api.common.core.AbstractConceptCodeWrapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptCodeWrapper extends AbstractConceptCodeWrapper<CodingDt> {

    public static ConceptCodeWrapper wrap(CodingDt coding) {
        return coding == null ? null : new ConceptCodeWrapper(coding);
    }

    public static List<IConceptCode> wrap(List<CodingDt> codings) {
        return codings == null ? Collections.emptyList() : codings.stream().map(coding -> ConceptCodeWrapper.wrap(coding)).collect(Collectors.toList());
    }

    public static List<IConceptCode> wrap(CodeableConceptDt codeableConcept) {
        return codeableConcept == null ? Collections.emptyList() : wrap(codeableConcept.getCoding());
    }

    public static CodingDt unwrap(IConceptCode code) {
        return code == null ? null : new CodingDt()
                .setSystem(code.getSystem())
                .setCode(code.getCode())
                .setDisplay(code.getText());
    }

    public static List<CodingDt> unwrap(List<IConceptCode> codes) {
        return codes == null ? Collections.emptyList() : codes.stream().map(code -> unwrap(code)).collect(Collectors.toList());
    }

    protected ConceptCodeWrapper(CodingDt coding) {
        super(coding);
    }

}
