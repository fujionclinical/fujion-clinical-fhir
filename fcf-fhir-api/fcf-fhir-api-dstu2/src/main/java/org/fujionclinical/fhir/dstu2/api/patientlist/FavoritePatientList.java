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
package org.fujionclinical.fhir.dstu2.api.patientlist;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.fujionclinical.fhir.api.common.patientlist.IPatientList;
import org.fujionclinical.fhir.api.common.patientlist.IPatientListFilter;

import java.util.Collection;

/**
 * Maintains a single list of favorite patient lists. The filters associated with this list are
 * essentially wrappers for other patient lists. Each filter is associated with an instance of
 * Favorite that wraps a patient list (and its active filter and date range if appropriate). The
 * list retrieval logic is essentially delegated to that of the wrapped list.
 */
public class FavoritePatientList extends AbstractPatientList {

    private Collection<PatientListItem> patients;

    public FavoritePatientList() {
        super("Favorites", "Favorite");
    }

    /**
     * Copy constructor.
     *
     * @param list Favorites list to copy.
     */
    public FavoritePatientList(FavoritePatientList list) {
        super(list);
    }

    /**
     * Creates the filter manager for this list.
     */
    @Override
    public FavoritePatientListFilterManager createFilterManager() {
        return new FavoritePatientListFilterManager(this);
    }

    /**
     * Adds a list to the favorites.
     *
     * @param list The list to add.
     */
    public void addFavorite(IPatientList<Patient> list) {
        if (list != null) {
            ((FavoritePatientListFilterManager) getFilterManager()).addEntity(new Favorite(list));
        }
    }

    /* ==================== IPatientList ==================== */

    /**
     * Resets the patient list when the filter changes.
     */
    @Override
    public void setActiveFilter(IPatientListFilter filter) {
        patients = null;
        super.setActiveFilter(filter);
    }

    /**
     * Forces this list to be the first.
     */
    @Override
    public int getSequence() {
        return Integer.MIN_VALUE;
    }

    /**
     * Returns the list of patients for the active filter. This simply calls the getListItems method
     * on the patient list associated with the currently selected filter.
     */
    @Override
    public Collection<PatientListItem> getListItems() {
        if (patients == null && getActiveFilter() != null) {
            IPatientListFilter filter = getActiveFilter();
            Favorite favorite = (Favorite) filter.getEntity();
            patients = favorite.getPatientList().getListItems();
        }

        return patients;
    }

    /**
     * Resets the patient list upon refresh.
     */
    @Override
    public void refresh() {
        super.refresh();
        patients = null;
    }

}
