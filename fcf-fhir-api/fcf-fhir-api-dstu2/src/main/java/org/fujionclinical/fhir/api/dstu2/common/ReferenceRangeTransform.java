package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.fujionclinical.api.model.core.IReferenceRange;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class ReferenceRangeTransform implements IWrapperTransform<IReferenceRange<Double>, Observation.ReferenceRange> {

    private static final ReferenceRangeTransform instance = new ReferenceRangeTransform();

    public static ReferenceRangeTransform getInstance() {
        return instance;
    }

    @Override
    public IReferenceRange<Double> _wrap(Observation.ReferenceRange value) {
        return new ReferenceRangeWrapper(value);
    }

    @Override
    public Observation.ReferenceRange newWrapped() {
        return new Observation.ReferenceRange();
    }

}
