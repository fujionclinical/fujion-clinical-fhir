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
package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.core.IWrapper;
import org.hl7.fhir.r5.model.Period;

import java.util.Date;

public class PeriodWrapper implements IPeriod, IWrapper<Period> {

    private final Period period;

    protected PeriodWrapper(Period period) {
        this.period = period;
    }

    @Override
    public Date getStartDate() {
        return period.getStart();
    }

    @Override
    public void setStartDate(Date date) {
        period.setStart(date);
    }

    @Override
    public Date getEndDate() {
        return period.getStart();
    }

    @Override
    public void setEndDate(Date date) {
        period.setEnd(date);
    }

    @Override
    public Period getWrapped() {
        return period;
    }

}
