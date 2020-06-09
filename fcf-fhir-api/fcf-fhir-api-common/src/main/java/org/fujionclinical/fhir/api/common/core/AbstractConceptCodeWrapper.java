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

import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IWrapper;
import org.hl7.fhir.instance.model.api.IBaseCoding;

public abstract class AbstractConceptCodeWrapper<T extends IBaseCoding> implements IConceptCode, IWrapper<T> {

    private final T coding;

    protected AbstractConceptCodeWrapper(T coding) {
        this.coding = coding;
    }

    @Override
    public String getSystem() {
        return coding.getSystem();
    }

    @Override
    public void setSystem(String system) {
        coding.setSystem(system);
    }

    @Override
    public String getCode() {
        return coding.getCode();
    }

    @Override
    public void setCode(String code) {
        coding.setCode(code);
    }

    @Override
    public String getText() {
        return coding.getDisplay();
    }

    @Override
    public void setText(String text) {
        coding.setDisplay(text);
    }

    @Override
    public T getWrapped() {
        return coding;
    }

}
