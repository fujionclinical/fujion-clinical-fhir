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
package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.StringDt;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.WrappedList;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.fhir.api.common.core.FhirUtil;

import java.util.Collections;
import java.util.List;

public class PersonNameWrapper extends AbstractWrapper<HumanNameDt> implements IPersonName {

    private final List<String> givenNames;

    private final List<String> prefixes;

    private final List<String> suffixes;

    protected PersonNameWrapper(HumanNameDt name) {
        super(name);
        this.givenNames = new WrappedList<>(name.getGiven(), StringTransform.getInstance());
        this.prefixes = new WrappedList<>(name.getPrefix(), StringTransform.getInstance());
        this.suffixes = new WrappedList<>(name.getSuffix(), StringTransform.getInstance());
    }

    @Override
    public String getFamilyName() {
        return getWrapped().getFamilyAsSingleString();
    }

    @Override
    public void setFamilyName(String familyName) {
        getWrapped().setFamily(Collections.singletonList(new StringDt(familyName)));
    }

    @Override
    public List<String> getGivenNames() {
        return givenNames;
    }

    @Override
    public List<String> getPrefixes() {
        return prefixes;
    }

    @Override
    public List<String> getSuffixes() {
        return suffixes;
    }

    @Override
    public PersonNameUse getUse() {
        return FhirUtil.convertEnum(getWrapped().getUse(), PersonNameUse.class);
    }

    @Override
    public void setUse(PersonNameUse category) {
        getWrapped().setUse(FhirUtil.convertEnum(category, NameUseEnum.class));
    }

    @Override
    public String toString() {
        return asString();
    }

}
