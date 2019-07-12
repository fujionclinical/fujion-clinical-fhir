/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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
package org.fujionclinical.fhir.lib.patientselection.core.r4;

import org.fujion.common.MiscUtil;

/**
 * Base patient selector factory from which other factories should descend.
 */
public class PatientSelectorFactoryBase implements IPatientSelectorFactory {
    
    private String factoryBeanId;
    
    private final String displayName;
    
    private final Class<? extends IPatientSelector> patientSelectorClass;
    
    protected PatientSelectorFactoryBase(String displayName, Class<? extends IPatientSelector> patientSelectorClass) {
        this.displayName = displayName;
        this.patientSelectorClass = patientSelectorClass;
    }
    
    /**
     * Creates the patient selection component.
     */
    @Override
    public IPatientSelector create() {
        try {
            return patientSelectorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String getFactoryBeanId() {
        return factoryBeanId;
    }
    
    /* package */void setFactoryBeanId(String factoryBeanId) {
        this.factoryBeanId = factoryBeanId;
    }
}
