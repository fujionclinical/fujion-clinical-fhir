package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IRatio;
import org.hl7.fhir.r5.model.Ratio;

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
