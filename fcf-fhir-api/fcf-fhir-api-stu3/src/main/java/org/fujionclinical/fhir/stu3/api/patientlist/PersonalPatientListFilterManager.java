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
package org.fujionclinical.fhir.stu3.api.patientlist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.property.PropertyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter manager for personal patient lists.
 */
public class PersonalPatientListFilterManager extends AbstractPatientListFilterManager {


    private static final Log log = LogFactory.getLog(PersonalPatientListFilterManager.class);

    private static final String INSTANCE_NAME = "$";

    private String filterProperty;

    public PersonalPatientListFilterManager(PersonalPatientList patientList) {
        super(patientList, PatientListUtil.createImmutableSet(FilterCapability.values()));
    }

    /**
     * Returns the property used to store the filter list.
     *
     * @return The filter property name.
     */
    private String getFilterProperty() {
        if (filterProperty == null) {
            filterProperty = ((PersonalPatientList) getPatientList()).getPropertyName();
        }

        return filterProperty;
    }

    /**
     * Initialize the filter list. This is pulled from the same property using an application
     * instance id of "@".
     */
    @Override
    protected List<AbstractPatientListFilter> initFilters() {
        if (filters == null) {
            log.debug("Retrieving personal list names...");
            filters = new ArrayList<>();
            List<String> values = PropertyUtil.getValues(getFilterProperty(), INSTANCE_NAME);

            if (values != null) {
                for (String value : values) {
                    filters.add(new PersonalPatientListFilter(value));
                }
            }
        }

        return filters;
    }

    /**
     * Save filters to the filter property.
     */
    @Override
    protected void saveFilters() {
        List<String> list = new ArrayList<>();

        for (AbstractPatientListFilter filter : initFilters()) {
            list.add(filter.serialize());
        }

        try {
            PropertyUtil.saveValues(getFilterProperty(), INSTANCE_NAME, false, list);
        } catch (Exception e) {
            log.error("Error saving personal list filters.", e);
        }
    }

    /**
     * Force reload of filter property upon filter refresh.
     */
    @Override
    protected void refreshFilters() {
        filterProperty = null;
        super.refreshFilters();
    }

    /**
     * Creates a filter wrapping the specified entity.
     */
    @Override
    protected AbstractPatientListFilter createFilter(Object entity) {
        return new PersonalPatientListFilter(entity);
    }

    /**
     * Creates a filter from its serialized form.
     */
    @Override
    protected AbstractPatientListFilter deserializeFilter(String serializedEntity) {
        return createFilter(serializedEntity);
    }

    /**
     * Deletes the associated personal list when a filter is removed.
     */
    @Override
    public void removeFilter(AbstractPatientListFilter filter) {
        ((PersonalPatientList) getPatientList()).deleteList(filter);
        super.removeFilter(filter);
    }

}
