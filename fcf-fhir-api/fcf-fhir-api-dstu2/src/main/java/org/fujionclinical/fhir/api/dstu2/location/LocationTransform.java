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
package org.fujionclinical.fhir.api.dstu2.location;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.LocationStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.LocationTypeEnum;
import org.fujion.common.CollectionUtil;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.location.ILocation;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtilDstu2;
import org.fujionclinical.fhir.api.dstu2.transform.BaseResourceTransform;
import org.fujionclinical.fhir.api.dstu2.transform.ContactPointTransform;

import java.util.List;

public class LocationTransform extends BaseResourceTransform<ILocation, Location> {

    private static final LocationTransform instance = new LocationTransform();

    public static LocationTransform getInstance() {
        return instance;
    }

    private LocationTransform() {
        super(ILocation.class, Location.class);
    }

    @Override
    protected ILocation newLogical() {
        return new org.fujionclinical.api.model.location.Location();
    }

    @Override
    protected Location newNative() {
        return new Location();
    }

    @Override
    protected List<IdentifierDt> getIdentifiers(Location location) {
        return location.getIdentifier();
    }

    @Override
    public Location _fromLogicalModel(ILocation src) {
        Location dest = super._fromLogicalModel(src);
        dest.setTelecom(ContactPointTransform.getInstance().fromLogicalModelAsList(src.getContactPoints()));
        dest.setStatus(CoreUtil.enumToEnum(src.getStatus(), LocationStatusEnum.class));
        dest.setName(src.getName());
        dest.setDescription(src.getDescription());
        dest.setType(FhirUtilDstu2.convertConceptToEnum(CollectionUtil.getFirst(src.getTypes()), LocationTypeEnum.class));
        return dest;
    }

    @Override
    public ILocation _toLogicalModel(Location src) {
        ILocation dest = super._toLogicalModel(src);
        dest.setContactPoints(ContactPointTransform.getInstance().toLogicalModelAsList(src.getTelecom()));
        dest.setStatus(CoreUtil.stringToEnum(src.getStatus(), ILocation.LocationStatus.class));
        dest.setName(src.getName());
        dest.setDescription(src.getDescription());
        dest.addTypes(FhirUtilDstu2.convertEnumToConcept(src.getType()));
        return dest;
    }

}
