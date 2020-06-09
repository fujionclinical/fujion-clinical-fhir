package org.fujionclinical.fhir.api.dstu2.location;

import ca.uhn.fhir.model.dstu2.resource.Location;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.location.ILocation;
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
