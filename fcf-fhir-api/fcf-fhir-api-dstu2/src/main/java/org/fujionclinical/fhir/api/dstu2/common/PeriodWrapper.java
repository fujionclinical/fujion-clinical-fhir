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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IPeriod;

import java.util.Date;

public class PeriodWrapper extends AbstractWrapper<PeriodDt> implements IPeriod {

    protected PeriodWrapper(PeriodDt period) {
        super(period);
    }

    @Override
    public Date getStartDate() {
        return getWrapped().getStart();
    }

    @Override
    public void setStartDate(Date date) {
        getWrapped().setStart(date, TemporalPrecisionEnum.SECOND);
    }

    @Override
    public Date getEndDate() {
        return getWrapped().getStart();
    }

    @Override
    public void setEndDate(Date date) {
        getWrapped().setEnd(date, TemporalPrecisionEnum.SECOND);
    }

}
