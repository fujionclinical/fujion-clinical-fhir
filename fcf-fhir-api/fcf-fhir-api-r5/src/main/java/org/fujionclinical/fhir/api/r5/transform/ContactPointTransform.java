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

import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.ContactPoint;
import org.fujionclinical.api.model.impl.ContactPointImpl;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class ContactPointTransform extends AbstractDatatypeTransform<ContactPoint, org.hl7.fhir.r5.model.ContactPoint> {

    private static final ContactPointTransform instance = new ContactPointTransform();

    public static ContactPointTransform getInstance() {
        return instance;
    }

    private ContactPointTransform() {
        super(ContactPoint.class, org.hl7.fhir.r5.model.ContactPoint.class);
    }

    @Override
    public org.hl7.fhir.r5.model.ContactPoint _fromLogicalModel(ContactPoint src) {
        org.hl7.fhir.r5.model.ContactPoint dest = new org.hl7.fhir.r5.model.ContactPoint();
        dest.setSystem(CoreUtil.enumToEnum(src.getSystem(), org.hl7.fhir.r5.model.ContactPoint.ContactPointSystem.class));
        dest.setValue(src.getValue());
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), org.hl7.fhir.r5.model.ContactPoint.ContactPointUse.class));
        dest.setRank(src.getRank());
        dest.setPeriod(PeriodTransform.getInstance().fromLogicalModel(src.getPeriod()));
        return dest;
    }

    @Override
    public ContactPoint _toLogicalModel(org.hl7.fhir.r5.model.ContactPoint src) {
        ContactPoint dest = new ContactPointImpl();
        dest.setSystem(CoreUtil.enumToEnum(src.getSystem(), ContactPoint.ContactPointSystem.class));
        dest.setValue(src.getValue());
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), ContactPoint.ContactPointUse.class));
        dest.setRank(src.getRank());
        dest.setPeriod(PeriodTransform.getInstance().toLogicalModel(src.getPeriod()));
        return dest;
    }

}
