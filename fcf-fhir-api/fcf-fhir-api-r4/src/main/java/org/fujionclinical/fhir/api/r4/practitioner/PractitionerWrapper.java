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
package org.fujionclinical.fhir.api.r4.practitioner;

import org.fujionclinical.api.model.core.*;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.practitioner.IPractitioner;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.r4.common.*;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Practitioner;

import java.util.List;

public class PractitionerWrapper extends BaseResourceWrapper<Practitioner> implements IPractitioner {

    private final List<IPersonName> names;

    private final List<IConcept> languages;

    private final List<IContactPoint> contactPoints;

    private final List<IPostalAddress> addresses;

    private final List<IAttachment> photos;

    protected PractitionerWrapper(Practitioner practitioner) {
        super(practitioner);
        names = PersonNameTransform.getInstance().wrap(practitioner.getName());
        languages = ConceptTransform.getInstance().wrap(practitioner.getCommunication());
        contactPoints = ContactPointTransform.getInstance().wrap(practitioner.getTelecom());
        addresses = PostalAddressTransform.getInstance().wrap(practitioner.getAddress());
        photos = AttachmentTransform.getInstance().wrap(practitioner.getPhoto());
    }

    @Override
    protected List<Identifier> _getIdentifiers() {
        return getWrapped().getIdentifier();
    }

    @Override
    public List<IPersonName> getNames() {
        return names;
    }

    @Override
    public Gender getGender() {
        return FhirUtil.convertEnum(getWrapped().getGender(), Gender.class, Gender.OTHER);
    }

    @Override
    public void setGender(Gender gender) {
        getWrapped().setGender(FhirUtil.convertEnum(gender, Enumerations.AdministrativeGender.class, Enumerations.AdministrativeGender.OTHER));
    }

    @Override
    public ConceptCode getBirthSex() {
        return null;
    }

    @Override
    public ConceptCode getEthnicity() {
        return null;
    }

    @Override
    public MaritalStatus getMaritalStatus() {
        return null;
    }

    @Override
    public ConceptCode getRace() {
        return null;
    }

    @Override
    public DateTimeWrapper getBirthDate() {
        return FhirUtil.convertDate(getWrapped().getBirthDate());
    }

    @Override
    public void setBirthDate(DateTimeWrapper date) {
        getWrapped().setBirthDate(FhirUtil.convertDate(date));
    }

    @Override
    public DateTimeWrapper getDeceasedDate() {
        return null;
    }

    @Override
    public List<IConcept> getLanguages() {
        return languages;
    }

    @Override
    public IConcept getPreferredLanguage() {
        return hasLanguage() ? getLanguages().get(0) : null;
    }

    @Override
    public List<IAttachment> getPhotos() {
        return photos;
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return contactPoints;
    }

    @Override
    public List<IPostalAddress> getAddresses() {
        return addresses;
    }

}
