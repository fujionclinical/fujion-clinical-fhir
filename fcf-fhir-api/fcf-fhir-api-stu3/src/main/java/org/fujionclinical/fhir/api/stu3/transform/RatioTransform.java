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

import org.fujionclinical.api.model.core.IRatio;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.dstu3.model.Ratio;

public class RatioTransform extends AbstractDatatypeTransform<IRatio, Ratio> {

    private static final RatioTransform instance = new RatioTransform();

    public static RatioTransform getInstance() {
        return instance;
    }

    private RatioTransform() {
        super(IRatio.class, Ratio.class);
    }

    @Override
    public Ratio _fromLogicalModel(IRatio src) {
        Ratio dest = new Ratio();
        dest.setNumerator(QuantityTransform.getInstance().fromLogicalModel(src.getNumerator()));
        dest.setDenominator(QuantityTransform.getInstance().fromLogicalModel(src.getDenominator()));
        return dest;
    }

    @Override
    public IRatio _toLogicalModel(Ratio src) {
        IRatio dest = new org.fujionclinical.api.model.impl.Ratio();
        dest.setNumerator(QuantityTransform.getInstance().toLogicalModel(src.getNumerator()));
        dest.setDenominator(QuantityTransform.getInstance().toLogicalModel(src.getDenominator()));
        return dest;
    }

}
