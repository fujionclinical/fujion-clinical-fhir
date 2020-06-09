package org.fujionclinical.fhir.api.r5.location;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.location.ILocation;
import org.hl7.fhir.r5.model.Location;
import org.springframework.beans.BeanUtils;

public class LocationTransform implements IWrapperTransform<ILocation, Location> {

    public static final LocationTransform instance = new LocationTransform();

    @Override
    public Location _unwrap(ILocation value) {
        Location location = new Location();
        ILocation wrapper = wrap(location);
        BeanUtils.copyProperties(location, wrapper);
        return location;
    }

    @Override
    public ILocation _wrap(Location value) {
        return new LocationWrapper(value);
    }

}
