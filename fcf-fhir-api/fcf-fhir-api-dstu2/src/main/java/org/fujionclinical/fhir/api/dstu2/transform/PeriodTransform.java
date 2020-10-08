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

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.impl.Period;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class PeriodTransform extends AbstractDatatypeTransform<IPeriod, PeriodDt> {

    private static final PeriodTransform instance = new PeriodTransform();

    public static PeriodTransform getInstance() {
        return instance;
    }

    private PeriodTransform() {
        super(IPeriod.class, PeriodDt.class);
    }

    @Override
    public PeriodDt _fromLogicalModel(IPeriod src) {
        PeriodDt dest = new PeriodDt();
        dest.setStart(DateTimeTransform.getInstance().fromLogicalModel(src.getStart()));
        dest.setEnd(DateTimeTransform.getInstance().fromLogicalModel(src.getEnd()));
        return dest;
    }

    @Override
    public IPeriod _toLogicalModel(PeriodDt src) {
        IPeriod dest = new Period();
        dest.setStart(DateTimeTransform.getInstance().toLogicalModel(src.getStartElement()));
        dest.setEnd(DateTimeTransform.getInstance().toLogicalModel(src.getEndElement()));
        return dest;
    }

}
