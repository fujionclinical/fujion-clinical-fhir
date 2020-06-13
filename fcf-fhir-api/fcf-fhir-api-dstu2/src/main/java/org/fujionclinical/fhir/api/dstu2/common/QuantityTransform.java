package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class QuantityTransform implements IWrapperTransform<IQuantity<Double>, QuantityDt> {

    private static final QuantityTransform instance = new QuantityTransform();

    public static QuantityTransform getInstance() {
        return instance;
    }

    @Override
    public IQuantity<Double> _wrap(QuantityDt value) {
        return new QuantityWrapper(value);
    }

    @Override
    public QuantityDt newWrapped() {
        return new QuantityDt();
    }

}
