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

import edu.utah.kmm.cool.common.MiscUtils;
import edu.utah.kmm.model.cool.terminology.ConceptReference;
import edu.utah.kmm.model.cool.terminology.ConceptReferenceSetImpl;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r4.model.Quantity;

public class QuantityTransform extends AbstractDatatypeTransform<IQuantity<Double>, Quantity> {

    private static final QuantityTransform instance = new QuantityTransform();

    public static QuantityTransform getInstance() {
        return instance;
    }

    private QuantityTransform() {
        super(CoreUtil.cast(IQuantity.class), Quantity.class);
    }

    @Override
    public Quantity _fromLogicalModel(IQuantity<Double> src) {
        Quantity dest = new Quantity();
        ConceptReference unit = MiscUtils.asNull(() -> src.getUnit().getFirstConcept());
        dest.setCode(unit == null ? null : unit.getCode());
        dest.setSystem(unit == null ? null : unit.getSystemAsString());
        dest.setUnit(unit == null ? null : unit.getPreferredName());
        dest.setValue(src.getValue());
        return dest;
    }

    @Override
    public IQuantity<Double> _toLogicalModel(Quantity src) {
        IQuantity dest = new org.fujionclinical.api.model.impl.Quantity();
        dest.setUnit(new ConceptReferenceSetImpl(src.getSystem(), src.getCode(), src.getUnit()));
        dest.setValue(src.getValue());
        return dest;
    }

}
