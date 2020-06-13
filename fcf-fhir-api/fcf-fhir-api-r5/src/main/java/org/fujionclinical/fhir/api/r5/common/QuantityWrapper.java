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
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Quantity;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

public class QuantityWrapper extends AbstractWrapper<Quantity> implements IQuantity<Double> {

    private final IConceptCode unit = new IConceptCode() {
        @Override
        public String getSystem() {
            return getWrapped().getSystem();
        }

        @Override
        public void setSystem(String system) {
            getWrapped().setSystem(system);
        }

        @Override
        public String getCode() {
            return getWrapped().getCode();
        }

        @Override
        public void setCode(String code) {
            getWrapped().setCode(code);
        }

        @Override
        public String getText() {
            return getWrapped().getUnit();
        }

        @Override
        public void setText(String text) {
            getWrapped().setUnit(text);
        }
    };

    protected QuantityWrapper(Quantity wrapped) {
        super(wrapped);
    }

    @Override
    public Double getValue() {
        BigDecimal value = getWrapped().getValue();
        return value == null ? null : value.doubleValue();
    }

    @Override
    public void setValue(Double value) {
        getWrapped().setValue(value);
    }

    @Override
    public QuantityComparator getComparator() {
        return FhirUtil.convertEnum(getWrapped().getComparator(), QuantityComparator.class);
    }

    @Override
    public void setComparator(QuantityComparator value) {
        getWrapped().setComparator(FhirUtil.convertEnum(value, Enumerations.QuantityComparator.class));
    }

    @Override
    public IConceptCode getUnit() {
        return unit;
    }

    @Override
    public void setUnit(IConceptCode value) {
        BeanUtils.copyProperties(value, unit);
    }

}
