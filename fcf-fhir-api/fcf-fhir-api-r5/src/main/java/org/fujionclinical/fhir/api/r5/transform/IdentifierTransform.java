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
package org.fujionclinical.fhir.api.r5.transform;

import edu.utah.kmm.model.cool.core.datatype.IdentifierExImpl;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r5.model.Identifier;

public class IdentifierTransform extends AbstractDatatypeTransform<edu.utah.kmm.model.cool.core.datatype.Identifier, Identifier> {

    private static final IdentifierTransform instance = new IdentifierTransform();

    public static IdentifierTransform getInstance() {
        return instance;
    }

    private IdentifierTransform() {
        super(edu.utah.kmm.model.cool.core.datatype.Identifier.class, Identifier.class);
    }

    @Override
    public Identifier _fromLogicalModel(edu.utah.kmm.model.cool.core.datatype.Identifier src) {
        Identifier dest = new Identifier();
        dest.setSystem(src.getSystem().toString());
        dest.setId(src.getId());
        dest.setType(ConceptTransform.getInstance().fromLogicalModel(src.getType()));
        return dest;
    }

    @Override
    public edu.utah.kmm.model.cool.core.datatype.Identifier _toLogicalModel(Identifier src) {
        edu.utah.kmm.model.cool.core.datatype.Identifier dest = new IdentifierExImpl(src.getSystem(), src.getId());
        dest.setType(ConceptTransform.getInstance().toLogicalModel(src.getType()));
        return dest;
    }

}
