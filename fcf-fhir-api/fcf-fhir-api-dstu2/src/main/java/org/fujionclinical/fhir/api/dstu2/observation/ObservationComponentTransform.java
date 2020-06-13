package org.fujionclinical.fhir.api.dstu2.observation;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.observation.IObservationType;

public class ObservationComponentTransform implements IWrapperTransform<IObservationType, Observation.Component> {

    private static final ObservationComponentTransform instance = new ObservationComponentTransform();

    public static ObservationComponentTransform getInstance() {
        return instance;
    }

    @Override
    public IObservationType _wrap(Observation.Component value) {
        return new ObservationComponentWrapper(value);
    }

    @Override
    public Observation.Component newWrapped() {
        return new Observation.Component();
    }

}
