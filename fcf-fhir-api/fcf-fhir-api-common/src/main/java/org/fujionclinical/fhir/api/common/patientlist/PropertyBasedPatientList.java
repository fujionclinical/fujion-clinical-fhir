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
package org.fujionclinical.fhir.api.common.patientlist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.property.PropertyUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for patient lists that are stored in properties. The filter associated with
 * this list type is typically the name of a stored list (though this default behavior can be
 * overridden). This name becomes the application instance value when storing list contents to and
 * retrieving from the associated property. If the derived class does not implement filters, the
 * null application instance value will be used.
 */
public abstract class PropertyBasedPatientList extends AbstractPatientList {

    private static final Log log = LogFactory.getLog(PropertyBasedPatientList.class);

    private final String propertyName;

    private List<IPatientListItem> pplList;

    private boolean changed;

    /**
     * Creates an instance of this list using the specified parameters.
     *
     * @param name         The list name.
     * @param entityName   The name of the entity type associated with any filters.
     * @param propertyName The name of the property where list contents are stored.
     */
    protected PropertyBasedPatientList(
            String name,
            String entityName,
            String propertyName,
            IPatientAdapterFactory patientAdapterFactory) {
        super(name, entityName, patientAdapterFactory);
        this.propertyName = propertyName;
    }

    /**
     * Copy constructor.
     *
     * @param list The source list to copy.
     */
    protected PropertyBasedPatientList(PropertyBasedPatientList list) {
        super(list);
        this.propertyName = list.propertyName;
    }

    /**
     * Removes all occurrences of the specified patient from the list.
     *
     * @param patient The patient to remove.
     */
    protected void removePatient(IPatientAdapter patient) {
        getListItems();
        IPatientListItem item;

        while ((item = PatientListUtil.findListItem(patient, pplList)) != null) {
            removeItem(item);
        }
    }

    /**
     * Removes a patient list item.
     *
     * @param item The patient list item to remove.
     */
    protected void removeItem(IPatientListItem item) {
        pplList.remove(item);
        changed = true;
    }

    /**
     * Adds the specified patient to the list. Any existing occurrences of that patient will first
     * be removed (i.e., duplicates are automatically eliminated).
     *
     * @param patient Patient to be added.
     * @param top     If true, the patient is added to the beginning of the list. If false, the patient
     *                is added to the end.
     */
    protected void addPatient(
            IPatientAdapter patient,
            boolean top) {
        int max = getListSizeMax();

        if (max > 0 && patient != null) {
            removePatient(patient);
            trimList(max - 1);
            addItem(new PatientListItem(patient), top);
        }
    }

    /**
     * Adds a patient list item.
     *
     * @param item The item to add.
     * @param top  If true, the item is added to the beginning of the list. If false, the item is
     *             added to the end.
     */
    protected void addItem(
            IPatientListItem item,
            boolean top) {
        getListItems();

        if (pplList.contains(item)) {
            return;
        }

        if (top) {
            pplList.add(0, item);
        } else {
            pplList.add(item);
        }

        changed = true;
    }

    /**
     * Returns the name of the property under which patient lists are stored.
     *
     * @return The property name.
     */
    protected String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the patient list.
     *
     * @return Patient list.
     */
    @Override
    public Collection<IPatientListItem> getListItems() {
        if (this.pplList == null) {
            this.pplList = new ArrayList<>();

            if (!isFiltered() || getActiveFilter() != null) {
                try {
                    String filterName = getListName();
                    int max = getListSizeMax();
                    List<String> patids = PropertyUtil.getValues(propertyName, filterName);

                    if (patids != null) {
                        for (String patientId : patids) {
                            if (pplList.size() >= max) {
                                break;
                            }

                            try {
                                addPatient(getPatient(patientId), false);
                            } catch (Exception e) {
                                // NOP
                            }
                        }
                    }

                } catch (Exception e) {
                    log.error("Error while retrieving patient list.", e);
                }
            }
        }

        return Collections.unmodifiableList(this.pplList);
    }

    /**
     * Forces a refresh of the list.
     */
    @Override
    public void refresh() {
        super.refresh();
        pplList = null;
        changed = false;
    }

    /**
     * Sets the active filter which, for personal lists, determines the list name.
     */
    @Override
    public void setActiveFilter(IPatientListFilter filter) {
        refresh();
        super.setActiveFilter(filter);
    }

    /**
     * Trim the list to the specified maximum size.
     *
     * @param maxSize The maximum list size.
     */
    private void trimList(int maxSize) {
        maxSize = maxSize < 0 ? 0 : maxSize;

        while (this.pplList.size() > maxSize) {
            this.pplList.remove(this.pplList.size() - 1);
        }
    }

    /**
     * Saves the patient list for the user. First, trims the list if it exceeds the maximum
     * allowable length.
     *
     * @param sort If true, the list is sorted before saving.
     */
    protected void saveList(boolean sort) {
        if (!changed) {
            return;
        }

        changed = false;
        getListItems();

        if (sort) {
            Collections.sort(pplList);
        }

        try {
            List<String> patids = new ArrayList<>();
            trimList(getListSizeMax());

            for (IPatientListItem item : pplList) {
                IPatientAdapter pat = item.getPatient();

                if (pat != null) {
                    patids.add(pat.getResource().getIdElement().getIdPart());
                }
            }

            saveProperty(propertyName, patids);
        } catch (Exception e) {
            log.error("Error while saving patient list.", e);
        }
    }

    /**
     * Saves the patient id string to the appropriate property value. The default is to save as a
     * user level preference. This can be overridden by subclasses for different behavior.
     *
     * @param propertyName the name of the property definition to use when saving
     * @param patids       List of patient logical ids.
     */
    protected void saveProperty(
            String propertyName,
            List<String> patids) {
        PropertyUtil.saveValues(propertyName, getListName(), false, patids);
    }

    /**
     * Deletes the list represented by the specified filter.
     *
     * @param filter The list filter.
     */
    protected void deleteList(IPatientListFilter filter) {
        if (getActiveFilter() != filter) {
            setActiveFilter(filter);
        }

        getListItems();
        pplList.clear();
        saveList(false);
        refresh();
    }

    /**
     * Returns the name of the personal list.
     *
     * @return The list name.
     */
    protected String getListName() {
        return getActiveFilter() == null ? null : getActiveFilter().getName();
    }

    /**
     * Returns the setting for the maximum list size for the list. By default, there is no effective
     * size limit.
     *
     * @return The maximum list size.
     */
    protected int getListSizeMax() {
        return Integer.MAX_VALUE;
    }

}
