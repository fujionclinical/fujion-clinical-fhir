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
package org.fujionclinical.fhir.dstu2.api.patientlist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.property.PropertyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter manager for maintaining favorite patient lists. The entity for a favorite patient list is
 * an instance of Favorite which essentially wraps another patient list (and its active filter and
 * date range, if appropriate).
 */
public class FavoritePatientListFilterManager extends AbstractPatientListFilterManager {


    private static final Log log = LogFactory.getLog(FavoritePatientListFilterManager.class);

    private static final String FAVORITES_PROPERTY = "FCF.PATIENT.LIST.FAVORITES";

    /**
     * Create the filter manager for the specified favorite patient list.
     *
     * @param patientList The patient list.
     */
    public FavoritePatientListFilterManager(FavoritePatientList patientList) {
        super(patientList,
                PatientListUtil.createImmutableSet(FilterCapability.RENAME, FilterCapability.MOVE, FilterCapability.REMOVE));
    }

    @Override
    protected List<AbstractPatientListFilter> initFilters() {
        if (filters == null) {
            filters = new ArrayList<>();

            List<String> values = PropertyUtil.getValues(FAVORITES_PROPERTY, null);

            if (values != null) {
                for (String value : values) {
                    try {
                        filters.add(createFilter(new Favorite(value)));
                    } catch (Exception e) {
                        log.error("Error creating favorite list item.", e);
                    }
                }
            }
        }
        return filters;
    }

    /**
     * Saves the current favorites list to a property.
     */
    @Override
    protected void saveFilters() {
        List<String> values = new ArrayList<>();

        for (AbstractPatientListFilter filter : initFilters()) {
            values.add(filter.getEntity().toString());
        }

        try {
            PropertyUtil.saveValues(FAVORITES_PROPERTY, null, false, values);
        } catch (Exception e) {
            log.error("Error saving favorites.", e);
        }
    }

    /**
     * Creates a filter for the specified entity.
     *
     * @param entity The entity (an instance of Favorite).
     * @return A filter appropriate for this list type.
     */
    @Override
    public AbstractPatientListFilter createFilter(Object entity) {
        return new FavoritePatientListFilter((Favorite) entity);
    }

    /**
     * Creates a filter from its serialized form.
     *
     * @param serializedEntity The serialized form of the filter.
     * @return A new filter.
     */
    @Override
    protected AbstractPatientListFilter deserializeFilter(String serializedEntity) {
        return createFilter(new Favorite(serializedEntity));
    }

}
