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
package org.fujionclinical.fhir.api.stu3.patient;

import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.fhir.api.stu3.common.FhirUtilStu3;
import org.fujionclinical.fhir.api.stu3.transform.*;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.List;
import java.util.stream.Collectors;

public class PatientTransform extends BaseResourceTransform<IPatient, Patient> {

    private static final PatientTransform instance = new PatientTransform();

    public static PatientTransform getInstance() {
        return instance;
    }

    private PatientTransform() {
        super(IPatient.class, Patient.class);
    }

    @Override
    protected IPatient newLogical() {
        return new org.fujionclinical.api.model.patient.Patient();
    }

    @Override
    protected Patient newNative() {
        return new Patient();
    }

    @Override
    public IPatient _toLogicalModel(Patient src) {
        IPatient dest = super._toLogicalModel(src);
        dest.setNames(PersonNameTransform.getInstance().toLogicalModel(src.getName()));
        dest.setMRN(IdentifierTransform.getInstance().toLogicalModel(FhirUtilStu3.getMRN(src)));
        dest.setLanguages(ConceptTransform.getInstance().toLogicalModel(src.getCommunication().stream()
                .map(Patient.PatientCommunicationComponent::getLanguage)
                .collect(Collectors.toList())));
        dest.setContactPoints(ContactPointTransform.getInstance().toLogicalModel(src.getTelecom()));
        dest.setAddresses(PostalAddressTransform.getInstance().toLogicalModel(src.getAddress()));
        dest.setPhotos(AttachmentTransform.getInstance().toLogicalModel(src.getPhoto()));
        dest.setGender(CoreUtil.enumToEnum(src.getGender(), IPerson.Gender.class, IPerson.Gender.OTHER));
        dest.setMaritalStatus(FhirUtilStu3.convertMaritalStatus(src.getMaritalStatus()));
        dest.setBirthDate(DateTransform.getInstance().toLogicalModel(src.getBirthDate()));
        dest.setDeceasedDate(DateTimeTransform.getInstance().anyToLogicalModel(src.getDeceased()));
        return dest;
    }

    @Override
    public Patient _fromLogicalModel(IPatient src) {
        Patient dest = super._fromLogicalModel(src);
        dest.setName(PersonNameTransform.getInstance().fromLogicalModel(src.getNames()));
        // Note: MRN logic not needed because identifiers already copied.
        dest.setCommunication(src.getLanguages().stream()
                .map(language -> ConceptTransform.getInstance().fromLogicalModel(language))
                .map(language -> new Patient.PatientCommunicationComponent().setLanguage(language))
                .collect(Collectors.toList()));
        dest.setTelecom(ContactPointTransform.getInstance().fromLogicalModel(src.getContactPoints()));
        dest.setAddress(PostalAddressTransform.getInstance().fromLogicalModel(src.getAddresses()));
        dest.setPhoto(AttachmentTransform.getInstance().fromLogicalModel(src.getPhotos()));
        dest.setGender(CoreUtil.enumToEnum(src.getGender(), Enumerations.AdministrativeGender.class, Enumerations.AdministrativeGender.OTHER));
        dest.setMaritalStatus(FhirUtilStu3.convertMaritalStatus(src.getMaritalStatus()));
        dest.setDeceased(PrimitiveTransform.getInstance().fromLogicalModel(src.getDeceasedDate()));
        return dest;
    }

    @Override
    protected List<Identifier> getIdentifiers(Patient patient) {
        return patient.getIdentifier();
    }

}
