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
package org.fujionclinical.fhir.api.r4.transform;

import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IContactPoint;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r4.model.ContactPoint;

public class ContactPointTransform extends AbstractDatatypeTransform<IContactPoint, ContactPoint> {

    private static final ContactPointTransform instance = new ContactPointTransform();

    public static ContactPointTransform getInstance() {
        return instance;
    }

    private ContactPointTransform() {
        super(IContactPoint.class, ContactPoint.class);
    }

    @Override
    public ContactPoint _fromLogicalModel(IContactPoint src) {
        ContactPoint dest = new ContactPoint();
        dest.setSystem(CoreUtil.enumToEnum(src.getSystem(), ContactPoint.ContactPointSystem.class));
        dest.setValue(src.getValue());
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), ContactPoint.ContactPointUse.class));
        dest.setRank(src.getRank());
        dest.setPeriod(PeriodTransform.getInstance().fromLogicalModel(src.getPeriod()));
        return dest;
    }

    @Override
    public IContactPoint _toLogicalModel(ContactPoint src) {
        IContactPoint dest = new org.fujionclinical.api.model.impl.ContactPoint();
        dest.setSystem(CoreUtil.enumToEnum(src.getSystem(), IContactPoint.ContactPointSystem.class));
        dest.setValue(src.getValue());
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), IContactPoint.ContactPointUse.class));
        dest.setRank(src.getRank());
        dest.setPeriod(PeriodTransform.getInstance().toLogicalModel(src.getPeriod()));
        return dest;
    }

}
