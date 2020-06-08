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
package org.fujionclinical.fhir.api.r4.patient;

import org.apache.commons.lang3.BooleanUtils;
import org.fujionclinical.api.model.core.*;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.AbstractResourceWrapper;
import org.fujionclinical.fhir.api.r4.common.*;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.fujionclinical.fhir.api.r4.terminology.Constants.CODING_MRN;

public class PatientWrapper extends AbstractResourceWrapper<Patient> implements IPatient {

    private final List<IPersonName> names;

    private final List<IConcept> languages;

    private IdentifierWrapper mrn;

    public static PatientWrapper wrap(Patient patient) {
        return patient == null ? null : new PatientWrapper(patient);
    }

    public static Patient unwrap(IPatient patient) {
        if (patient == null) {
            return null;
        }

        if (patient instanceof PatientWrapper) {
            return ((PatientWrapper) patient).getWrapped();
        }

        PatientWrapper pt = wrap(new Patient());
        BeanUtils.copyProperties(patient, pt);
        return pt.getWrapped();
    }

    private PatientWrapper(Patient patient) {
        super(patient);
        names = PersonNameWrapper.wrap(patient.getName());
        mrn = IdentifierWrapper.wrap(FhirUtilR4.getMRN(patient));
        languages = patient.getCommunication().stream().map(comm -> ConceptWrapper.wrap(comm.getLanguage())).collect(Collectors.toList());
    }

    @Override
    public List<IIdentifier> getIdentifiers() {
        return getWrapped().getIdentifier().stream().map(identifier -> IdentifierWrapper.wrap(identifier)).collect(Collectors.toList());
    }

    @Override
    public IIdentifier getMRN() {
        return mrn;
    }

    @Override
    public void setMRN(IIdentifier mrn) {
        if (mrn == null) {
            Identifier ident = IdentifierWrapper.unwrap(mrn);
            ident.getType().addCoding(CODING_MRN);
            this.mrn = IdentifierWrapper.wrap(new Identifier());
        }
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
    public MaritalStatus getMaritalStatus() {
        return FhirUtilR4.convertMaritalStatus(getWrapped().getMaritalStatus());
    }

    @Override
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        getWrapped().setMaritalStatus(FhirUtilR4.convertMaritalStatus(maritalStatus));
    }

    @Override
    public Date getBirthDate() {
        return getWrapped().getBirthDate();
    }

    @Override
    public void setBirthDate(Date date) {
        getWrapped().setBirthDate(date);
    }

    @Override
    public Date getDeceasedDate() {
        DateTimeType deceased = getWrapped().hasDeceasedDateTimeType() ? getWrapped().getDeceasedDateTimeType() : null;
        return deceased == null ? null : deceased.getValue();
    }

    @Override
    public void setDeceasedDate(Date date) {
        getWrapped().setDeceased(new DateTimeType(date));
    }

    @Override
    public List<IPersonName> getNames() {
        return names;
    }

    @Override
    public List<IContactPoint> getContactPoints() {
        return getWrapped().getTelecom().stream().map(ContactPointWrapper::wrap).collect(Collectors.toList());
    }

    @Override
    public List<IPostalAddress> getAddresses() {
        return getWrapped().getAddress().stream().map(PostalAddressWrapper::wrap).collect(Collectors.toList());
    }

    @Override
    public List<IAttachment> getPhotos() {
        return getWrapped().getPhoto().stream().map(AttachmentWrapper::wrap).collect(Collectors.toList());
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
                .map(comm -> ConceptWrapper.wrap(comm.getLanguage()))
                .orElse(null);
    }

}
