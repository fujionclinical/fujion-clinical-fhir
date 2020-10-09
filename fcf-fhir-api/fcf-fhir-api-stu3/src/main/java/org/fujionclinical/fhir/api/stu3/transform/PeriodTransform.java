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

//import edu.utah.kmm.model.cool.core.datatype.Period;
import edu.utah.kmm.model.cool.core.datatype.PeriodImpl;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.dstu3.model.Period;

public class PeriodTransform extends AbstractDatatypeTransform<edu.utah.kmm.model.cool.core.datatype.Period, Period> {

    private static final PeriodTransform instance = new PeriodTransform();

    public static PeriodTransform getInstance() {
        return instance;
    }

    private PeriodTransform() {
        super(edu.utah.kmm.model.cool.core.datatype.Period.class, Period.class);
    }

    @Override
    public Period _fromLogicalModel(edu.utah.kmm.model.cool.core.datatype.Period src) {
        Period dest = new Period();
        dest.setStartElement(DateTimeTransform.getInstance().fromLogicalModel(src.getStart()));
        dest.setEndElement(DateTimeTransform.getInstance().fromLogicalModel(src.getEnd()));
        return dest;
    }

    @Override
    public edu.utah.kmm.model.cool.core.datatype.Period _toLogicalModel(Period src) {
        edu.utah.kmm.model.cool.core.datatype.Period dest = new PeriodImpl();
        dest.setStart(DateTimeTransform.getInstance().toLogicalModel(src.getStartElement()));
        dest.setEnd(DateTimeTransform.getInstance().toLogicalModel(src.getEndElement()));
        return dest;
    }

}
