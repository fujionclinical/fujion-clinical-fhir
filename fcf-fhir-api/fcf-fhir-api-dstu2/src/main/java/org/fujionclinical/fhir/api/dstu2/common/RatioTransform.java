package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import org.fujionclinical.api.model.core.IRatio;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class RatioTransform implements IWrapperTransform<IRatio, RatioDt> {

    @Override
    public IRatio _wrap(RatioDt value) {
        return new RatioWrapper(value);
    }

    @Override
    public RatioDt newWrapped() {
        return new RatioDt();
    }

}
