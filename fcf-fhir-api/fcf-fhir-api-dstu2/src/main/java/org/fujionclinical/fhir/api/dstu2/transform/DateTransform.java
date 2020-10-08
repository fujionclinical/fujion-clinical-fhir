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

import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.fujion.common.DateUtil;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DateTransform extends AbstractDatatypeTransform<LocalDate, DateDt> {

    private static final DateTransform instance = new DateTransform();

    public static DateTransform getInstance() {
        return instance;
    }

    private DateTransform() {
        super(LocalDate.class, DateDt.class);
    }

    @Override
    public DateDt _fromLogicalModel(LocalDate value) {
        return new DateDt(DateUtil.toDate(value));
    }

    public DateDt fromLogicalModel(LocalDateTime value) {
        return new DateDt(DateUtil.toDate(value));
    }

    @Override
    public LocalDate _toLogicalModel(DateDt value) {
        return toLogicalModel(value.getValue());
    }

    public LocalDate toLogicalModel(DateTimeDt value) {
        return toLogicalModel(value.getValue());
    }

    public LocalDate toLogicalModel(Date value) {
        return value == null ? null : DateUtil.toLocalDate(value);
    }

}
