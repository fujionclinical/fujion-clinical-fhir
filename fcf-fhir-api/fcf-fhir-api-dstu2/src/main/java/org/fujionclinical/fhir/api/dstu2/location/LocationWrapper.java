package org.fujionclinical.fhir.api.dstu2.location;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.LocationStatusEnum;
import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IContactPoint;
import org.fujionclinical.api.model.IWrapper;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.dstu2.common.ConceptWrapper;
import org.fujionclinical.fhir.api.dstu2.common.ContactPointWrapper;
import org.fujionclinical.fhir.api.dstu2.encounter.EncounterWrapper;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
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
        return FhirUtil.convertEnum(getWrapped().getStatusElement().getValueAsEnum(), LocationStatus.class);
    }

    @Override
    public void setStatus(LocationStatus status) {
        getWrapped().setStatus(FhirUtil.convertEnum(status, LocationStatusEnum.class));
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
        return Collections.singletonList(ConceptWrapper.wrap(getWrapped().getType()));
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return getWrapped().getTelecom().stream().map(contactPoint -> ContactPointWrapper.wrap(contactPoint)).collect(Collectors.toList());
    }

}
