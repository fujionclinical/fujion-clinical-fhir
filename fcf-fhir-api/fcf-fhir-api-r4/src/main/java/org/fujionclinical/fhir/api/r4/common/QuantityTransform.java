package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r4.model.Quantity;

public class QuantityTransform implements IWrapperTransform<IQuantity<Double>, Quantity> {

    private static final QuantityTransform instance = new QuantityTransform();

    public static QuantityTransform getInstance() {
        return instance;
    }

    @Override
    public IQuantity<Double> _wrap(Quantity value) {
        return new QuantityWrapper(value);
    }

    @Override
    public Quantity newWrapped() {
        return new Quantity();
    }

}
