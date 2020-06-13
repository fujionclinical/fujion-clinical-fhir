package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IRatio;

public class RatioWrapper extends AbstractWrapper<RatioDt> implements IRatio<Double> {

    private IQuantity<Double> numerator;

    private IQuantity<Double> denominator;

    protected RatioWrapper(RatioDt wrapped) {
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
