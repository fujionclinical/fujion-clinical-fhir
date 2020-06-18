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

import ca.uhn.fhir.model.api.IPrimitiveDatatype;
import ca.uhn.fhir.model.primitive.*;
import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

import java.math.BigDecimal;
import java.util.Date;

public class PrimitiveTransform extends AbstractDatatypeTransform<Object, IPrimitiveDatatype> {

    private static final PrimitiveTransform instance = new PrimitiveTransform();

    public static PrimitiveTransform getInstance() {
        return instance;
    }

    private PrimitiveTransform() {
        super(Object.class, IPrimitiveDatatype.class);
    }

    @Override
    public IPrimitiveDatatype _fromLogicalModel(Object src) {
        if (src instanceof String) {
            return new StringDt((String) src);
        } else if (src instanceof Boolean) {
            return new BooleanDt((Boolean) src);
        } else if (src instanceof Integer) {
            return new IntegerDt((Integer) src);
        } else if (src instanceof Double) {
            return new DecimalDt(BigDecimal.valueOf((Double) src));
        } else if (src instanceof DateTimeWrapper) {
            DateTimeWrapper dtw = (DateTimeWrapper) src;
            return dtw.hasTime() ? new DateTimeDt(dtw.getLegacyDate()) : new DateDt(dtw.getLegacyDate());
        } else {
            return notSupported();
        }
    }

    @Override
    public Object _toLogicalModel(IPrimitiveDatatype src) {
        Object value = src.getValue();
        return value instanceof Date ? new DateTimeWrapper((Date) value) : value;
    }

}
