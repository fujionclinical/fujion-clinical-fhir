package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.SimpleQuantity;

public class SimpleQuantityTransform implements IWrapperTransform<IQuantity<Double>, SimpleQuantity> {

    private static final SimpleQuantityTransform instance = new SimpleQuantityTransform();

    public static SimpleQuantityTransform getInstance() {
        return instance;
    }

    @Override
    public IQuantity<Double> _wrap(SimpleQuantity value) {
        return new QuantityWrapper(value);
    }

    @Override
    public SimpleQuantity newWrapped() {
        return new SimpleQuantity();
    }

}
