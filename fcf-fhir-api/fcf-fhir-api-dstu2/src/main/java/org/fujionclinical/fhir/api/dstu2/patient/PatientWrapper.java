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
package org.fujionclinical.fhir.api.dstu2.patient;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import org.apache.commons.lang3.BooleanUtils;
import org.fujionclinical.api.model.core.*;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.dstu2.common.*;
import org.fujionclinical.fhir.api.dstu2.terminology.Constants;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PatientWrapper extends BaseResourceWrapper<Patient> implements IPatient {

    private final List<IPersonName> names;

    private final List<IConcept> languages;

    private final List<IContactPoint> contactPoints;

    private final List<IPostalAddress> addresses;

    private final List<IAttachment> photos;

    private IIdentifier mrn;

    protected PatientWrapper(Patient patient) {
        super(patient);
        names = PersonNameTransform.instance.wrap(patient.getName());
        mrn = IdentifierTransform.instance.wrap(FhirUtilDstu2.getMRN(patient));
        languages = ConceptTransform.instance.wrap(patient.getCommunication().stream()
                .map(comm -> comm.getLanguage())
                .collect(Collectors.toList()));
        contactPoints = ContactPointTransform.instance.wrap(patient.getTelecom());
        addresses = PostalAddressTransform.instance.wrap(patient.getAddress());
        photos = AttachmentTransform.instance.wrap(patient.getPhoto());
    }

    @Override
    protected List<IdentifierDt> _getIdentifiers() {
        return getWrapped().getIdentifier();
    }

    @Override
    public IIdentifier getMRN() {
        return mrn;
    }

    @Override
    public void setMRN(IIdentifier mrn) {
        if (mrn == null) {
            IdentifierDt ident = IdentifierTransform.instance.unwrap(mrn);
            ident.getType().addCoding(Constants.CODING_MRN);
            this.mrn = IdentifierTransform.instance.wrap(new IdentifierDt());
        }
    }

    @Override
    public Gender getGender() {
        return FhirUtil.convertEnum(getWrapped().getGender(), Gender.class, Gender.OTHER);
    }

    @Override
    public void setGender(Gender gender) {
        getWrapped().setGender(FhirUtil.convertEnum(gender, AdministrativeGenderEnum.class, AdministrativeGenderEnum.OTHER));
    }

    @Override
    public MaritalStatus getMaritalStatus() {
        return FhirUtilDstu2.convertMaritalStatus(getWrapped().getMaritalStatus().getValueAsEnum());
    }

    @Override
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        getWrapped().setMaritalStatus(FhirUtilDstu2.convertMaritalStatus(maritalStatus));
    }

    @Override
    public Date getBirthDate() {
        return getWrapped().getBirthDate();
    }

    @Override
    public void setBirthDate(Date date) {
        getWrapped().setBirthDateWithDayPrecision(date);
    }

    @Override
    public Date getDeceasedDate() {
        IDatatype deceased = getWrapped().getDeceased();
        return deceased instanceof DateDt ? ((DateDt) deceased).getValue() : null;
    }

    @Override
    public void setDeceasedDate(Date date) {
        getWrapped().setDeceased(new DateDt(date));
    }

    @Override
    public List<IPersonName> getNames() {
        return names;
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return contactPoints;
    }

    @Override
    public List<IPostalAddress> getAddresses() {
        return addresses;
    }

    @Override
    public List<IAttachment> getPhotos() {
        return photos;
    }

    @Override
    public List<IConcept> getLanguages() {
        return languages;
    }

    @Override
    public IConcept getPreferredLanguage() {
        return getWrapped().getCommunication().stream()
                .filter(comm -> BooleanUtils.isTrue(comm.getPreferred()))
                .findFirst()
                .map(comm -> ConceptTransform.instance.wrap(comm.getLanguage()))
                .orElse(null);
    }

}
