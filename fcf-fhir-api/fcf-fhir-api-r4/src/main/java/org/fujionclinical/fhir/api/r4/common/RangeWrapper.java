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
package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IRange;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;

public class RangeWrapper extends AbstractWrapper<Range> implements IRange<Double> {

    protected RangeWrapper(Range wrapped) {
        super(wrapped);
    }

    @Override
    public Double getLow() {
        Quantity value = getWrapped().getLow();
        return value.isEmpty() ? null : value.getValue().doubleValue();
    }

    @Override
    public void setLow(Double value) {
        Quantity quantity = new Quantity();
        quantity.setValue(value);
        getWrapped().setLow(quantity);
    }

    @Override
    public Double getHigh() {
        Quantity value = getWrapped().getHigh();
        return value.isEmpty() ? null : value.getValue().doubleValue();
    }

    @Override
    public void setHigh(Double value) {
        Quantity quantity = new Quantity();
        quantity.setValue(value);
        getWrapped().setHigh(quantity);
    }

}
