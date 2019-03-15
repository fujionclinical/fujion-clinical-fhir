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
package org.fujionclinical.fhir.dstu3.api.common;

import org.apache.commons.lang.StringUtils;

/**
 * Base class for search criteria.
 */
public abstract class SearchCriteria {
    
    private final String validationFailureMessage;
    
    private String id;
    
    private int maximum;
    
    protected SearchCriteria(String validationFailureMessage) {
        this.validationFailureMessage = validationFailureMessage;
    }
    
    /**
     * Returns true if the current criteria settings meet the minimum requirements for a search.
     * 
     * @return True if minimum search requirements have been met.
     */
    protected boolean isValid() {
        return id != null;
    }
    
    /**
     * Validates that the current criteria settings meet the minimum requirements for a search. If
     * not, throws a run-time exception describing the deficiency.
     */
    public void validate() {
        if (!isValid()) {
            throw new SearchException(validationFailureMessage);
        }
    }
    
    /**
     * Returns the maximum hits criterion.
     * 
     * @return Maximum hits criterion.
     */
    public int getMaximum() {
        return maximum;
    }
    
    /**
     * Sets the maximum hits criterion.
     * 
     * @param maximum Maximum.
     */
    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }
    
    /**
     * Returns the domain identifier.
     * 
     * @return Domain identifier.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the domain identifier
     * 
     * @param id Domain identifier.
     */
    public void setId(String id) {
        this.id = StringUtils.trimToNull(id);
    }
    
    /**
     * Returns true if no criteria have been set.
     * 
     * @return True if no criteria have been set.
     */
    public boolean isEmpty() {
        return id == null;
    }
    
}
