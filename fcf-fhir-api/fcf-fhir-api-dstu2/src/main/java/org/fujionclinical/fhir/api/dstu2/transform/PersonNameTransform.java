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
package org.fujionclinical.fhir.api.dstu2.transform;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.StringDt;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.person.PersonName;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

import java.util.Collections;

public class PersonNameTransform extends AbstractDatatypeTransform<IPersonName, HumanNameDt> {

    private static final PersonNameTransform instance = new PersonNameTransform();

    public static PersonNameTransform getInstance() {
        return instance;
    }

    private PersonNameTransform() {
        super(IPersonName.class, HumanNameDt.class);
    }

    @Override
    public HumanNameDt _fromLogicalModel(IPersonName src) {
        HumanNameDt dest = new HumanNameDt();
        StringDt familyName = src.hasFamilyName() ? new StringDt(src.getFamilyName()) : null;
        dest.setFamily(familyName == null ? null : Collections.singletonList(familyName));
        dest.setGiven(StringTransform.getInstance().fromLogicalModelAsList(src.getGivenNames()));
        src.getPrefixes().forEach(dest::addPrefix);
        src.getSuffixes().forEach(dest::addSuffix);
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), NameUseEnum.class));
        return dest;
    }

    @Override
    public IPersonName _toLogicalModel(HumanNameDt src) {
        IPersonName dest = new PersonName();
        dest.setFamilyName(src.getFamily().isEmpty() ? null : src.getFamilyAsSingleString());
        dest.setGivenNames(StringTransform.getInstance().toLogicalModelAsList(src.getGiven()));
        src.getPrefix().forEach(prefix -> dest.addPrefixes(prefix.getValue()));
        src.getSuffix().forEach(suffix -> dest.addSuffixes(suffix.getValue()));
        dest.setUse(CoreUtil.stringToEnum(src.getUse(), IPersonName.PersonNameUse.class));
        return dest;
    }

}
