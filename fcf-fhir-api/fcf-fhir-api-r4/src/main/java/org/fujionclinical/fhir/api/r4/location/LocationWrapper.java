package org.fujionclinical.fhir.api.r4.location;

import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IContactPoint;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.r4.common.ConceptWrapper;
import org.fujionclinical.fhir.api.r4.common.ContactPointWrapper;
import org.hl7.fhir.r4.model.Location;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class LocationWrapper extends ResourceWrapper<Location> implements ILocation {

    public static LocationWrapper wrap(Location location) {
        return location == null ? null : new LocationWrapper(location);
    }

    public static Location unwrap(ILocation location) {
        if (location == null) {
            return null;
        }

        if (location instanceof LocationWrapper) {
            return ((LocationWrapper) location).getWrapped();
        }

        LocationWrapper wrapper = wrap(new Location());
        BeanUtils.copyProperties(location, wrapper);
        return wrapper.getWrapped();
    }

    private LocationWrapper(Location location) {
        super(location);
    }

    @Override
    public LocationStatus getStatus() {
        return FhirUtil.convertEnum(getWrapped().getStatus(), LocationStatus.class);
    }

    @Override
    public void setStatus(LocationStatus status) {
        getWrapped().setStatus(FhirUtil.convertEnum(status, Location.LocationStatus.class));
    }

    @Override
    public String getName() {
        return getWrapped().getName();
    }

    @Override
    public void setName(String name) {
        getWrapped().setName(name);
    }

    @Override
    public String getDescription() {
        return getWrapped().getDescription();
    }

    @Override
    public void setDescription(String description) {
        getWrapped().setDescription(description);
    }

    @Override
    public List<IConcept> getTypes() {
        return getWrapped().getType().stream().map(ConceptWrapper::wrap).collect(Collectors.toList());
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return getWrapped().getTelecom().stream().map(ContactPointWrapper::wrap).collect(Collectors.toList());
    }

}
