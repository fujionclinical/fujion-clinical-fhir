/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.fhir.api.stu3.location;

import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IContactPoint;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.stu3.common.ConceptWrapper;
import org.fujionclinical.fhir.api.stu3.common.ContactPointWrapper;
import org.hl7.fhir.dstu3.model.Location;
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
        return Collections.singletonList(ConceptWrapper.wrap(getWrapped().getType()));
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return getWrapped().getTelecom().stream().map(ContactPointWrapper::wrap).collect(Collectors.toList());
    }

}
