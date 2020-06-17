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

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.impl.ConceptCode;
import org.fujionclinical.api.model.impl.Quantity;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class QuantityTransform extends AbstractDatatypeTransform<IQuantity<Double>, QuantityDt> {

    private static final QuantityTransform instance = new QuantityTransform();

    public static QuantityTransform getInstance() {
        return instance;
    }

    private QuantityTransform() {
        super(CoreUtil.cast(IQuantity.class), QuantityDt.class);
    }

    @Override
    public QuantityDt _fromLogicalModel(IQuantity<Double> src) {
        QuantityDt dest = new QuantityDt();
        IConceptCode unit = src.getUnit();
        dest.setCode(unit == null ? null : unit.getCode());
        dest.setSystem(unit == null ? null : unit.getSystem());
        dest.setUnit(unit == null ? null : unit.getText());
        dest.setValue(src.getValue());
        return dest;
    }

    @Override
    public IQuantity<Double> _toLogicalModel(QuantityDt src) {
        IQuantity<Double> dest = new Quantity<>();
        dest.setUnit(new ConceptCode(src.getSystem(), src.getCode(), src.getUnit()));
        dest.setValue(src.getValueElement().hasValue() ? src.getValue().doubleValue() : null);
        return dest;
    }

}
