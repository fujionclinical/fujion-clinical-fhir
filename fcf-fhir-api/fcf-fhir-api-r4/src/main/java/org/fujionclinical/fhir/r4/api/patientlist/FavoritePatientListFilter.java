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
 * Filter for the favorite patient list.
 */
public class FavoritePatientListFilter extends AbstractPatientListFilter {
    
    public FavoritePatientListFilter(Favorite entity) {
        super(entity);
    }
    
    public FavoritePatientListFilter(String value) {
        super(value);
    }
    
    @Override
    protected String serialize() {
        return getEntity().toString();
    }
    
    @Override
    protected Favorite deserialize(String value) {
        return new Favorite(value);
    }
    
    @Override
    protected String initName() {
        return ((Favorite) getEntity()).getName();
    }

    @Override
    protected void setName(String name) {
        super.setName(name);
        ((Favorite) getEntity()).setName(name);
    }

}
