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
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IRatio;
import org.hl7.fhir.r4.model.Ratio;

public class RatioWrapper extends AbstractWrapper<Ratio> implements IRatio<Double> {

    private IQuantity<Double> numerator;

    private IQuantity<Double> denominator;

    protected RatioWrapper(Ratio wrapped) {
        super(wrapped);
        this.numerator = QuantityTransform.getInstance().wrap(wrapped.getNumerator());
        this.denominator = QuantityTransform.getInstance().wrap(wrapped.getDenominator());
    }

    @Override
    public IQuantity<Double> getNumerator() {
        return numerator;
    }

    @Override
    public void setNumerator(IQuantity<Double> value) {
        this.numerator = value;
        getWrapped().setNumerator(QuantityTransform.getInstance().unwrap(value));
    }

    @Override
    public IQuantity<Double> getDenominator() {
        return denominator;
    }

    @Override
    public void setDenominator(IQuantity<Double> value) {
        this.denominator = value;
        getWrapped().setDenominator(QuantityTransform.getInstance().unwrap(value));
    }

}
