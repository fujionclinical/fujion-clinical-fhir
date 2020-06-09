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
package org.fujionclinical.fhir.api.common.core;

import ca.uhn.fhir.model.api.Tag;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.instance.model.api.IBaseCoding;

public class TagTransform implements IWrapperTransform<IConceptCode, IBaseCoding> {

    public static final TagTransform instance = new TagTransform();

    @Override
    public IConceptCode _wrap(IBaseCoding value) {
        return new BaseConceptCodeWrapper<>(value);
    }

    @Override
    public IBaseCoding newWrapped() {
        return new Tag();
    }

}
