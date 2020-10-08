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
package org.fujionclinical.fhir.api.stu3.transform;

import org.fujion.common.DateUtil;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DateType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DateTransform extends AbstractDatatypeTransform<LocalDate, DateType> {

    private static final DateTransform instance = new DateTransform();

    public static DateTransform getInstance() {
        return instance;
    }

    private DateTransform() {
        super(LocalDate.class, DateType.class);
    }

    @Override
    public DateType _fromLogicalModel(LocalDate value) {
        return new DateType(DateUtil.toDate(value));
    }

    public DateType fromLogicalModel(LocalDateTime value) {
        return new DateType(DateUtil.toDate(value));
    }

    @Override
    public LocalDate _toLogicalModel(DateType value) {
        return toLogicalModel(value.getValue());
    }

    public LocalDate toLogicalModel(DateTimeType value) {
        return toLogicalModel(value.getValue());
    }

    public LocalDate toLogicalModel(Date value) {
        return value == null ? null : DateUtil.toLocalDate(value);
    }

}
