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
package org.fujionclinical.fhir.r4.api.patientlist;

/**
 * Filter associated with the personal patient list.  The entity associated with this filter is simply the
 * name of the personal list.
 */
public class PersonalPatientListFilter extends AbstractPatientListFilter {
    
    /**
     * Create a filter using the specified entity value (the personal list name).
     * 
     * @param value The name of the personal list.
     */
    public PersonalPatientListFilter(Object value) {
        super(value);
    }
    
    /**
     * Returns the serialized form of the filter entity (which is simply the list name).
     */
    @Override
    protected String serialize() {
        return getEntity().toString();
    }
    
    /**
     * Returns the deserialized form of the filter entity (which is the same as its serialized form).
     */
    @Override
    protected String deserialize(String value) {
        return value;
    }
    
    /**
     * Returns the initial default name for this filter.
     */
    @Override
    protected String initName() {
        return getEntity() == null ? "" : getEntity().toString();
    }

    /**
     * Sets the name for this filter, which also sets the associated entity.
     */
    @Override
    protected void setName(String name) {
        super.setName(name);
        setEntity(name);
    }

}
