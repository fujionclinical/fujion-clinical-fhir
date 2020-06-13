package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class SimpleQuantityTransform implements IWrapperTransform<IQuantity<Double>, SimpleQuantityDt> {

    private static final SimpleQuantityTransform instance = new SimpleQuantityTransform();

    public static SimpleQuantityTransform getInstance() {
        return instance;
    }

    @Override
    public IQuantity<Double> _wrap(SimpleQuantityDt value) {
        return new QuantityWrapper(value);
    }

    @Override
    public SimpleQuantityDt newWrapped() {
        return new SimpleQuantityDt();
    }

}
