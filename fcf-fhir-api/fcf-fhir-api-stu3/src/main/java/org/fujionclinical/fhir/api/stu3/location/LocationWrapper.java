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

import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IContactPoint;
import org.fujionclinical.api.model.location.ILocation;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.stu3.common.BaseResourceWrapper;
import org.fujionclinical.fhir.api.stu3.common.ConceptTransform;
import org.fujionclinical.fhir.api.stu3.common.ContactPointTransform;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Location;

import java.util.Collections;
import java.util.List;

public class LocationWrapper extends BaseResourceWrapper<Location> implements ILocation {

    private final List<IContactPoint> contactPoints;

    protected LocationWrapper(Location location) {
        super(location);
        this.contactPoints = ContactPointTransform.getInstance().wrap(location.getTelecom());
    }

    @Override
    protected List<Identifier> _getIdentifiers() {
        return getWrapped().getIdentifier();
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
        return Collections.singletonList(ConceptTransform.getInstance().wrap(getWrapped().getType()));
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return contactPoints;
    }

}
