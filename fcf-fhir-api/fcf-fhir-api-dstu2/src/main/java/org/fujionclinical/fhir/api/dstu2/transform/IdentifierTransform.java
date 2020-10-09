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

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierTypeCodesEnum;
import edu.utah.kmm.model.cool.core.datatype.Identifier;
import edu.utah.kmm.model.cool.core.datatype.IdentifierExImpl;
import edu.utah.kmm.model.cool.terminology.ConceptReference;
import org.fujion.common.CollectionUtil;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class IdentifierTransform extends AbstractDatatypeTransform<Identifier, IdentifierDt> {

    private static final IdentifierTransform instance = new IdentifierTransform();

    public static IdentifierTransform getInstance() {
        return instance;
    }

    private IdentifierTransform() {
        super(Identifier.class, IdentifierDt.class);
    }

    @Override
    public IdentifierDt _fromLogicalModel(Identifier src) {
        IdentifierDt dest = new IdentifierDt();
        dest.setSystem(src.getSystem().toString());
        dest.setValue(src.getId());
        ConceptReference code = !src.hasType() ? null : CollectionUtil.getFirst(src.getType().getBySystem("http://hl7.org/fhir/ValueSet/identifier-type"));
        dest.setType(code == null ? null : IdentifierTypeCodesEnum.forCode(code.getCode()));
        return dest;
    }

    @Override
    public Identifier _toLogicalModel(IdentifierDt src) {
        Identifier dest = new IdentifierExImpl(src.getSystem(), src.getValue());
        dest.setType(ConceptTransform.getInstance().toLogicalModel(src.getType()));
        return dest;
    }

}
