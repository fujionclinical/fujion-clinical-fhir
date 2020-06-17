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
package org.fujionclinical.fhir.api.r5.practitioner;

import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.api.model.practitioner.IPractitioner;
import org.fujionclinical.fhir.api.r5.transform.*;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.Practitioner;

import java.util.List;

public class PractitionerTransform extends BaseResourceTransform<IPractitioner, Practitioner> {

    private static final PractitionerTransform instance = new PractitionerTransform();

    public static PractitionerTransform getInstance() {
        return instance;
    }

    private PractitionerTransform() {
        super(IPractitioner.class, Practitioner.class);
    }

    @Override
    protected IPractitioner newLogical() {
        return new org.fujionclinical.api.model.practitioner.Practitioner();
    }

    @Override
    protected Practitioner newNative() {
        return new Practitioner();
    }

    @Override
    public Practitioner _fromLogicalModel(IPractitioner src) {
        Practitioner dest = super._fromLogicalModel(src);
        dest.setName(PersonNameTransform.getInstance().fromLogicalModel(src.getNames()));
        dest.setCommunication(ConceptTransform.getInstance().fromLogicalModel(src.getLanguages()));
        dest.setTelecom(ContactPointTransform.getInstance().fromLogicalModel(src.getContactPoints()));
        dest.setAddress(PostalAddressTransform.getInstance().fromLogicalModel(src.getAddresses()));
        dest.setPhoto(AttachmentTransform.getInstance().fromLogicalModel(src.getPhotos()));
        dest.setGender(CoreUtil.enumToEnum(src.getGender(), Enumerations.AdministrativeGender.class, Enumerations.AdministrativeGender.OTHER));
        return dest;
    }

    @Override
    public IPractitioner _toLogicalModel(Practitioner src) {
        IPractitioner dest = super._toLogicalModel(src);
        dest.setNames(PersonNameTransform.getInstance().toLogicalModel(src.getName()));
        dest.setLanguages(ConceptTransform.getInstance().toLogicalModel(src.getCommunication()));
        dest.setContactPoints(ContactPointTransform.getInstance().toLogicalModel(src.getTelecom()));
        dest.setAddresses(PostalAddressTransform.getInstance().toLogicalModel(src.getAddress()));
        dest.setPhotos(AttachmentTransform.getInstance().toLogicalModel(src.getPhoto()));
        dest.setGender(CoreUtil.enumToEnum(src.getGender(), IPerson.Gender.class, IPerson.Gender.OTHER));
        dest.setBirthDate(DateTransform.getInstance().toLogicalModel(src.getBirthDate()));
        return dest;
    }

    @Override
    protected List<Identifier> getIdentifiers(Practitioner practitioner) {
        return practitioner.getIdentifier();
    }

}
