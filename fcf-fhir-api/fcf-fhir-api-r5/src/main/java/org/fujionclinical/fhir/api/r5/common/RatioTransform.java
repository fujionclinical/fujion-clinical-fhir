package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IRatio;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r5.model.Ratio;

public class RatioTransform implements IWrapperTransform<IRatio, Ratio> {

    @Override
    public IRatio _wrap(Ratio value) {
        return new RatioWrapper(value);
    }

    @Override
    public Ratio newWrapped() {
        return new Ratio();
    }

}
