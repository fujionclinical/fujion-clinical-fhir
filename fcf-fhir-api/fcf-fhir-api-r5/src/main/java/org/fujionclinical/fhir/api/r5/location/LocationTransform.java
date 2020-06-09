package org.fujionclinical.fhir.api.r5.location;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.location.ILocation;
import org.hl7.fhir.r5.model.Location;

public class LocationTransform implements IWrapperTransform<ILocation, Location> {

    public static final LocationTransform instance = new LocationTransform();

    @Override
    public ILocation _wrap(Location value) {
        return new LocationWrapper(value);
    }

    @Override
    public Location newWrapped() {
        return new Location();
    }

}
