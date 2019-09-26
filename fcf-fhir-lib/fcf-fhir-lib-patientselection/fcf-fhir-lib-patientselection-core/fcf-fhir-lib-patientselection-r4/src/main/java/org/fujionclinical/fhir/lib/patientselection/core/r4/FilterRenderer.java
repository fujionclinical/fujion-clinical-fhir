/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2019 fujionclinical.org
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

import org.fujion.component.Listitem;
import org.fujion.model.IComponentRenderer;
import org.fujionclinical.fhir.r4.api.patientlist.AbstractPatientListFilter;

/**
 * Renderer for filters.
 */
public class FilterRenderer implements IComponentRenderer<Listitem, AbstractPatientListFilter> {
    
    private static final FilterRenderer instance = new FilterRenderer();
    
    /**
     * Return singleton instance.
     *
     * @return The filter renderer.
     */
    public static FilterRenderer getInstance() {
        return instance;
    }
    
    /**
     * Force singleton usage.
     */
    private FilterRenderer() {
        super();
    }
    
    /**
     * Render a list item.
     *
     * @param filter The associated PatientListFilter object.
     */
    @Override
    public Listitem render(AbstractPatientListFilter filter) {
        Listitem item = new Listitem(filter.getName());
        item.setHint(filter.getName());
        item.setData(filter);
        return item;
    }
    
}
