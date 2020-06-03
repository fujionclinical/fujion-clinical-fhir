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

import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import org.fujionclinical.api.model.IContactPoint;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IWrapper;
import org.fujionclinical.fhir.api.common.core.FhirUtil;

public class ContactPointWrapper implements IContactPoint, IWrapper<ContactPointDt> {

    private final ContactPointDt contactPoint;

    private PeriodWrapper period;

    public static ContactPointWrapper wrap(ContactPointDt contactPoint) {
        return contactPoint == null ? null : new ContactPointWrapper(contactPoint);
    }

    private ContactPointWrapper(ContactPointDt contactPoint) {
        this.contactPoint = contactPoint;
        period = PeriodWrapper.wrap(contactPoint.getPeriod());
    }

    @Override
    public ContactPointSystem getSystem() {
        return FhirUtil.convertEnum(contactPoint.getSystemElement().getValueAsEnum(), ContactPointSystem.class);
    }

    @Override
    public void setSystem(ContactPointSystem system) {
        contactPoint.setSystem(FhirUtil.convertEnum(system, ContactPointSystemEnum.class));
    }

    @Override
    public String getValue() {
        return contactPoint.getValue();
    }

    @Override
    public void setValue(String value) {
        contactPoint.setValue(value);
    }

    @Override
    public ContactPointUse getUse() {
        return FhirUtil.convertEnum(contactPoint.getUseElement().getValueAsEnum(), ContactPointUse.class);
    }

    @Override
    public void setUse(ContactPointUse use) {
        contactPoint.setUse(FhirUtil.convertEnum(use, ContactPointUseEnum.class));
    }

    @Override
    public Integer getRank() {
        return contactPoint.getRank();
    }

    @Override
    public void setRank(Integer rank) {
        contactPoint.setRank(rank);
    }

    @Override
    public IPeriod getPeriod() {
        return period;
    }

    @Override
    public void setPeriod(IPeriod period) {
        this.period = PeriodWrapper.wrap(PeriodWrapper.unwrap(period));
        contactPoint.setPeriod(this.period.getWrapped());
    }

    @Override
    public ContactPointDt getWrapped() {
        return contactPoint;
    }

}
