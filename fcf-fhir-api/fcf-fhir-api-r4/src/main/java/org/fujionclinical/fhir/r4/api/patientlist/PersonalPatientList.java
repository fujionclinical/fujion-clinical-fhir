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
package org.fujionclinical.fhir.r4.api.patientlist;

/**
 * Maintains personal patient lists.
 */
public class PersonalPatientList extends PropertyBasedPatientList implements IPatientListItemManager {

    /**
     * Creates an instance of a personal list.
     *
     * @param propertyName Name of the property where this list is to be stored.
     */
    public PersonalPatientList(String propertyName) {
        super("Personal Lists", "List", propertyName);
    }

    /**
     * Copy constructor.
     *
     * @param list The source list to copy.
     */
    public PersonalPatientList(PersonalPatientList list) {
        super(list);
    }

    /**
     * Creates the filter manager for this list.
     */
    @Override
    public PersonalPatientListFilterManager createFilterManager() {
        return new PersonalPatientListFilterManager(this);
    }

    /* ===================== IPatientListItemManager ===================== */

    @Override
    public void addItem(PatientListItem item) {
        super.addItem(item, false);
    }

    @Override
    public void removeItem(PatientListItem item) {
        super.removeItem(item);
    }

    @Override
    public void save() {
        saveList(true);
    }

}
