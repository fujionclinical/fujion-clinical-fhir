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

import org.fujion.common.DateRange;

import java.util.Collection;

/**
 * Interface for all patient lists.
 */
public interface IPatientList {
    
    /**
     * Returns the name assigned to this list. The name must be unique across all patient lists.
     * 
     * @return The filter's name.
     */
    String getName();
    
    /**
     * Returns the display name for this list. This should be a combination of the list's name and
     * any relevant state information associated with the list (like its current filter and date
     * range settings, if applicable).
     * 
     * @return The display name.
     */
    String getDisplayName();
    
    /**
     * Returns the display name of the entity type associated with filters (if any) for this list.
     * If a list does not support filters, this should return null.
     * 
     * @return The display name of the filters' entity type, or null if filters are not supported by
     *         the list.
     */
    String getEntityName();
    
    /**
     * The sorting sequence for this list. Lower values will cause the list to sort before other
     * lists.
     * 
     * @return The sorting sequence for the list.
     */
    int getSequence();
    
    /**
     * If true, the list supports date ranges in its retrieval logic.
     * 
     * @return True if date ranges are supported.
     */
    boolean isDateRangeRequired();
    
    /**
     * If true, the list is disabled for the current user and should not be displayed.
     * 
     * @return True if the list is disabled.
     */
    boolean isDisabled();
    
    /**
     * If true, the list supports filtering.
     * 
     * @return True if the list supports filters.
     */
    boolean isFiltered();
    
    /**
     * Returns the active filter applied to the list. If the list does not support filters, or if no
     * active filter has been set, this will return null.
     * 
     * @return The active filter.
     */
    AbstractPatientListFilter getActiveFilter();
    
    /**
     * Returns the patient list item manager associated with the list. If the list does not support
     * user management of its list items, this should return null.
     * 
     * @return The associated list item manager, or null if not applicable.
     */
    IPatientListItemManager getItemManager();
    
    /**
     * Returns the filter manager associated with the list. If the list does not support filters, or
     * if the list does not support user management of its filters, this should return null.
     * 
     * @return The associated filter manager, or null if not applicable.
     */
    IPatientListFilterManager getFilterManager();
    
    /**
     * Sets the active filter to the specified value. If the list does not support filters, this
     * request should be ignored.
     * 
     * @param filter The filter to become active.
     */
    void setActiveFilter(AbstractPatientListFilter filter);
    
    /**
     * Returns the date range associated with the list. If the list does not support date ranges, or
     * if no date range has been set, this should return null.
     * 
     * @return The current date range.
     */
    DateRange getDateRange();
    
    /**
     * Sets the date range to be applied to this patient list. If the list does not support date
     * ranges, the request will be ignored.
     * 
     * @param value The date range.
     */
    void setDateRange(DateRange value);
    
    /**
     * Returns the list of available filters for this patient list. If the list does not support
     * filters, this should return null.
     * 
     * @return A list of filters, or null if not applicable.
     */
    Collection<AbstractPatientListFilter> getFilters();
    
    /**
     * Returns a list of patient list items. The underlying logic should apply the active filter and
     * date range, if appropriate, in determining what items are returned. If any required
     * parameters are not yet set, this operation may return null.
     * 
     * @return A list of patient items, or null if the required parameters have not be set.
     */
    Collection<PatientListItem> getListItems();
    
    /**
     * Returns a fully cloned copy of this list.
     * 
     * @return A clone of the original list.
     */
    IPatientList copy();
    
    /**
     * Returns a fully cloned copy of this list, applying any serialized settings (active filter,
     * date range, etc).
     * 
     * @param serialized Serialized settings applicable for this list.
     * @return A clone of the original list.
     */
    IPatientList copy(String serialized);
    
    /**
     * Returns the serialized form of this list, including any active settings (active filter, date
     * range, etc) where applicable.
     * 
     * @return The serialized form of this list.
     */
    String serialize();
    
    /**
     * Forces a refresh of the patient items returned by this list.
     */
    void refresh();
    
    /**
     * Returns true if the list is in the process of being built.
     * 
     * @return The pending status.
     */
    boolean isPending();
    
}
