package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IReferenceRange;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Observation.ObservationReferenceRangeComponent;

public class ReferenceRangeTransform implements IWrapperTransform<IReferenceRange<Double>, ObservationReferenceRangeComponent> {

    private static final ReferenceRangeTransform instance = new ReferenceRangeTransform();

    public static ReferenceRangeTransform getInstance() {
        return instance;
    }

    @Override
    public IReferenceRange<Double> _wrap(ObservationReferenceRangeComponent value) {
        return new ReferenceRangeWrapper(value);
    }

    @Override
    public ObservationReferenceRangeComponent newWrapped() {
        return new ObservationReferenceRangeComponent();
    }

}
