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

import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class DateTimeTransform extends AbstractDatatypeTransform<DateTimeWrapper, DateTimeDt> {

    private static final DateTimeTransform instance = new DateTimeTransform();

    public static DateTimeTransform getInstance() {
        return instance;
    }

    private DateTimeTransform() {
        super(DateTimeWrapper.class, DateTimeDt.class);
    }

    @Override
    public DateTimeDt _fromLogicalModel(DateTimeWrapper value) {
        return new DateTimeDt(value.getLegacyDate());
    }

    @Override
    public DateTimeWrapper _toLogicalModel(DateTimeDt value) {
        return value.isEmpty() ? null : new DateTimeWrapper(value.getValue());
    }

}
