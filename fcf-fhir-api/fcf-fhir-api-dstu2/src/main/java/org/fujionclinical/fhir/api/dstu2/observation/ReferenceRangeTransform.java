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
package org.fujionclinical.fhir.api.dstu2.observation;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IReferenceRange;
import org.fujionclinical.api.model.impl.ReferenceRange;
import org.fujionclinical.fhir.api.common.transform.AbstractModelTransform;
import org.fujionclinical.fhir.api.dstu2.transform.ConceptTransform;
import org.fujionclinical.fhir.api.dstu2.transform.RangeTransform;
import org.fujionclinical.fhir.api.dstu2.transform.SimpleQuantityTransform;

public class ReferenceRangeTransform extends AbstractModelTransform<IReferenceRange<Double>, Observation.ReferenceRange> {

    private static final ReferenceRangeTransform instance = new ReferenceRangeTransform();

    public static ReferenceRangeTransform getInstance() {
        return instance;
    }

    private ReferenceRangeTransform() {
        super(CoreUtil.cast(IReferenceRange.class), Observation.ReferenceRange.class);
    }

    @Override
    public Observation.ReferenceRange _fromLogicalModel(IReferenceRange<Double> src) {
        Observation.ReferenceRange dest = new Observation.ReferenceRange();
        dest.setLow(SimpleQuantityTransform.getInstance().fromLogicalModel(src.getLow()));
        dest.setHigh(SimpleQuantityTransform.getInstance().fromLogicalModel(src.getHigh()));
        dest.setText(src.getDescription());
        dest.setMeaning(ConceptTransform.getInstance().fromLogicalModel(src.getType()));
        dest.setAge(RangeTransform.getInstance().fromLogicalModel(src.getAgeRange()));
        // appliesTo not supported in DSTU2
        return dest;
    }

    @Override
    public IReferenceRange<Double> _toLogicalModel(Observation.ReferenceRange src) {
        IReferenceRange<Double> dest = new ReferenceRange<>();
        dest.setLow(SimpleQuantityTransform.getInstance().toLogicalModel(src.getLow()));
        dest.setHigh(SimpleQuantityTransform.getInstance().toLogicalModel(src.getHigh()));
        dest.setDescription(src.getText());
        dest.setType(ConceptTransform.getInstance().toLogicalModel(src.getMeaning()));
        dest.setAgeRange(RangeTransform.getInstance().toLogicalModel(src.getAge()));
        // appliesTo not supported in DSTU2
        return dest;
    }

}
