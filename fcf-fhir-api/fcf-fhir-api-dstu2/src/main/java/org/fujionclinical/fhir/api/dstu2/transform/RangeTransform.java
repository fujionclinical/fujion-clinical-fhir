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

import ca.uhn.fhir.model.dstu2.composite.RangeDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IRange;
import org.fujionclinical.api.model.impl.Range;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class RangeTransform extends AbstractDatatypeTransform<IRange<Double>, RangeDt> {

    private static final RangeTransform instance = new RangeTransform();

    public static RangeTransform getInstance() {
        return instance;
    }

    private RangeTransform() {
        super(CoreUtil.cast(IRange.class), RangeDt.class);
    }

    @Override
    public RangeDt _fromLogicalModel(IRange<Double> src) {
        RangeDt dest = new RangeDt();
        dest.setHigh(new SimpleQuantityDt(src.getHigh()));
        dest.setLow(new SimpleQuantityDt(src.getLow()));
        return dest;
    }

    @Override
    public IRange<Double> _toLogicalModel(RangeDt src) {
        return new Range(src.getLow().getValue().doubleValue(), src.getHigh().getValue().doubleValue());
    }

}