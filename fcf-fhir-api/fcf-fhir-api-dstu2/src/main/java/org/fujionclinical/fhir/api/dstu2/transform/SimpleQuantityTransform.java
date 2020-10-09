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

import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import edu.utah.kmm.cool.common.MiscUtils;
import edu.utah.kmm.model.cool.core.datatype.QuantityEx;
import edu.utah.kmm.model.cool.core.datatype.QuantityExImpl;
import edu.utah.kmm.model.cool.terminology.ConceptReference;
import edu.utah.kmm.model.cool.terminology.ConceptReferenceSetImpl;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class SimpleQuantityTransform extends AbstractDatatypeTransform<QuantityEx<Double>, SimpleQuantityDt> {

    private static final SimpleQuantityTransform instance = new SimpleQuantityTransform();

    public static SimpleQuantityTransform getInstance() {
        return instance;
    }

    private SimpleQuantityTransform() {
        super(CoreUtil.cast(QuantityEx.class), SimpleQuantityDt.class);
    }

    @Override
    public SimpleQuantityDt _fromLogicalModel(QuantityEx<Double> src) {
        SimpleQuantityDt dest = new SimpleQuantityDt();
        ConceptReference unit = MiscUtils.asNull(() -> src.getUnit().getFirstConcept());
        dest.setCode(unit == null ? null : unit.getCode());
        dest.setSystem(unit == null ? null : unit.getSystem().toString());
        dest.setUnit(unit == null ? null : unit.getPreferredName());
        dest.setValue(src.getValue());
        return dest;
    }

    @Override
    public QuantityEx<Double> _toLogicalModel(SimpleQuantityDt src) {
        QuantityEx dest = new QuantityExImpl();
        dest.setUnit(new ConceptReferenceSetImpl(src.getSystem(), src.getCode(), src.getUnit()));
        dest.setValue(src.getValue());
        return dest;
    }

}
