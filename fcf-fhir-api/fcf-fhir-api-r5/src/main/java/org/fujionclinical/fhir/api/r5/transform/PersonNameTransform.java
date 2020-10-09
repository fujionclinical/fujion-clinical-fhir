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
package org.fujionclinical.fhir.api.r5.transform;

import edu.utah.kmm.model.cool.foundation.datatype.PersonNameUse;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.person.PersonNameImpl;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r5.model.HumanName;

public class PersonNameTransform extends AbstractDatatypeTransform<IPersonName, HumanName> {

    private static final PersonNameTransform instance = new PersonNameTransform();

    public static PersonNameTransform getInstance() {
        return instance;
    }

    private PersonNameTransform() {
        super(IPersonName.class, HumanName.class);
    }

    @Override
    public HumanName _fromLogicalModel(IPersonName src) {
        HumanName dest = new HumanName();
        dest.setFamily(src.getFamily());
        dest.setGiven(StringTransform.getInstance().fromLogicalModelAsList(src.getGiven()));
        src.getPrefixes().forEach(dest::addPrefix);
        src.getSuffixes().forEach(dest::addSuffix);
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), HumanName.NameUse.class));
        return dest;
    }

    @Override
    public IPersonName _toLogicalModel(HumanName src) {
        IPersonName dest = new PersonNameImpl();
        dest.setFamily(src.getFamily());
        dest.setGiven(StringTransform.getInstance().toLogicalModelAsList(src.getGiven()));
        src.getPrefix().forEach(prefix -> dest.addPrefixes(prefix.getValue()));
        src.getSuffix().forEach(suffix -> dest.addSuffixes(suffix.getValue()));
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), PersonNameUse.class));
        return dest;
    }

}
