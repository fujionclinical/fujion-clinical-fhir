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
package org.fujionclinical.fhir.api.stu3.transform;

import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.impl.ConceptCode;
import org.fujionclinical.api.model.impl.Quantity;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.dstu3.model.SimpleQuantity;

public class SimpleQuantityTransform extends AbstractDatatypeTransform<IQuantity<Double>, SimpleQuantity> {

    private static final SimpleQuantityTransform instance = new SimpleQuantityTransform();

    public static SimpleQuantityTransform getInstance() {
        return instance;
    }

    private SimpleQuantityTransform() {
        super(CoreUtil.cast(IQuantity.class), SimpleQuantity.class);
    }

    @Override
    public SimpleQuantity _fromLogicalModel(IQuantity<Double> src) {
        SimpleQuantity dest = new SimpleQuantity();
        IConceptCode unit = src.getUnit();
        dest.setCode(unit == null ? null : unit.getCode());
        dest.setSystem(unit == null ? null : unit.getSystem());
        dest.setUnit(unit == null ? null : unit.getText());
        dest.setValue(src.getValue());
        return dest;
    }

    @Override
    public IQuantity<Double> _toLogicalModel(SimpleQuantity src) {
        Quantity dest = new Quantity();
        dest.setUnit(new ConceptCode(src.getSystem(), src.getCode(), src.getUnit()));
        dest.setValue(src.getValue());
        return dest;
    }

}
