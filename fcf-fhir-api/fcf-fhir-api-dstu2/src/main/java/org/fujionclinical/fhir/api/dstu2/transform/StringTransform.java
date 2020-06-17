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

import ca.uhn.fhir.model.primitive.StringDt;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class StringTransform extends AbstractDatatypeTransform<String, StringDt> {

    private static final StringTransform instance = new StringTransform();

    public static StringTransform getInstance() {
        return instance;
    }

    private StringTransform() {
        super(String.class, StringDt.class);
    }

    @Override
    public StringDt _fromLogicalModel(String value) {
        return new StringDt(value);
    }

    @Override
    public String _toLogicalModel(StringDt value) {
        return value.getValue();
    }

}
