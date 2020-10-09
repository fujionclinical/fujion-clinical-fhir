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

import edu.utah.kmm.model.cool.core.datatype.RatioImpl;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Ratio;

import java.math.BigDecimal;

public class RatioTransform extends AbstractDatatypeTransform<edu.utah.kmm.model.cool.core.datatype.Ratio, Ratio> {

    private static final RatioTransform instance = new RatioTransform();

    public static RatioTransform getInstance() {
        return instance;
    }

    private RatioTransform() {
        super(edu.utah.kmm.model.cool.core.datatype.Ratio.class, Ratio.class);
    }

    @Override
    public Ratio _fromLogicalModel(edu.utah.kmm.model.cool.core.datatype.Ratio src) {
        Ratio dest = new Ratio();
        dest.setNumerator(createQuantity(src.getNumerator()));
        dest.setDenominator(createQuantity(src.getDenominator()));
        return dest;
    }

    private Quantity createQuantity(Object value) {
        if (value == null) {
            return null;
        }

        Quantity quantity = new Quantity();
        Number num = value instanceof Number ? (Number) value : null;

        if (num instanceof Long || num instanceof Integer) {
            quantity.setValue(num.longValue());
        } else if (num instanceof Double || num instanceof Float) {
            quantity.setValue(num.doubleValue());
        } else if (num instanceof BigDecimal) {
            quantity.setValue((BigDecimal) num);
        } else {
            throw new IllegalArgumentException("Invalid datatype for ratio: " + value.getClass());
        }

        return quantity;
    }

    @Override
    public edu.utah.kmm.model.cool.core.datatype.Ratio _toLogicalModel(Ratio src) {
        edu.utah.kmm.model.cool.core.datatype.Ratio dest = new RatioImpl<>();
        dest.setNumerator(src.getNumerator().getValue());
        dest.setDenominator(src.getDenominator().getValue());
        return dest;
    }

}
