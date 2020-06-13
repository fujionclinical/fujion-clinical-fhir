package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.valueset.QuantityComparatorEnum;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

public class QuantityWrapper extends AbstractWrapper<QuantityDt> implements IQuantity<Double> {

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

    protected QuantityWrapper(QuantityDt wrapped) {
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
        return FhirUtil.convertEnum(getWrapped().getComparatorElement().getValueAsEnum(), QuantityComparator.class);
    }

    @Override
    public void setComparator(QuantityComparator value) {
        getWrapped().setComparator(FhirUtil.convertEnum(value, QuantityComparatorEnum.class));
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
