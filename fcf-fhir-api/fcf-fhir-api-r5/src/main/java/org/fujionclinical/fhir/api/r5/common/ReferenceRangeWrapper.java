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
package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IReferenceRange;
import org.hl7.fhir.r5.model.Observation.ObservationReferenceRangeComponent;

public class ReferenceRangeWrapper extends AbstractWrapper<ObservationReferenceRangeComponent> implements IReferenceRange<Double> {

    private IConcept type;

    private IQuantity<Double> low;

    private IQuantity<Double> high;

    protected ReferenceRangeWrapper(ObservationReferenceRangeComponent wrapped) {
        super(wrapped);
        this.type = ConceptTransform.getInstance().wrap(wrapped.getType());
        this.low = QuantityTransform.getInstance().wrap(wrapped.getLow());
        this.high = QuantityTransform.getInstance().wrap(wrapped.getHigh());
    }

    @Override
    public IConcept getType() {
        return type;
    }

    @Override
    public void setType(IConcept value) {
        type = value;
        getWrapped().setType(ConceptTransform.getInstance().unwrap(value));
    }

    @Override
    public String getDescription() {
        return getWrapped().getText();
    }

    @Override
    public void setDescription(String value) {
        getWrapped().setText(value);
    }

    @Override
    public IQuantity<Double> getLow() {
        return low;
    }

    @Override
    public void setLow(IQuantity<Double> value) {
        getWrapped().setLow(value == null ? null : QuantityTransform.getInstance().unwrap(value));
    }

    @Override
    public IQuantity<Double> getHigh() {
        return high;
    }

    @Override
    public void setHigh(IQuantity<Double> value) {
        getWrapped().setHigh(value == null ? null : QuantityTransform.getInstance().unwrap(value));
    }

}
