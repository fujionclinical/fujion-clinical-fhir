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

import java.util.List;
import java.util.Set;

/**
 * Defines methods for managing a user-manageable list.
 */
public interface IPatientListFilterManager {

    /**
     * Each member represents a specific capability that may be supported by a given filter manager.
     */
    enum FilterCapability {ADD, REMOVE, MOVE, RENAME}

    /**
     * Adds a new filter to the filter list.
     *
     * @param entity The entity to be associated with the new filter.
     * @return The newly created filter.
     */
    IPatientListFilter addFilter(Object entity);

    /**
     * Removes the specified filter from the filter list.
     *
     * @param filter The filter to be removed.
     */
    void removeFilter(IPatientListFilter filter);

    /**
     * Moves the specified filter to the specified position within the filter list.  If the filter does not
     * exist within the list, the request is ignored.
     *
     * @param filter The filter to be moved.
     * @param index  The position within the filter list where the filter is to be moved.
     */
    void moveFilter(
            IPatientListFilter filter,
            int index);

    /**
     * Renames a filter.
     *
     * @param filter  The filter to be renamed.
     * @param newName The new name for the filter.  If the filter name does not meet the naming requirements for the filter type,
     *                a runtime exception will be raised.
     */
    void renameFilter(
            IPatientListFilter filter,
            String newName);

    /**
     * Implement logic to initialize filters.
     *
     * @return The filter list (never null).
     */
    List<IPatientListFilter> initFilters();

    /**
     * Force a refresh of the filter list.
     */
    void refreshFilters();

    /**
     * Returns the filter matching the specified name, or null if not found.
     *
     * @param filterName The filter name.
     * @return The associated filter.
     */
    IPatientListFilter getFilterByName(String filterName);

    /**
     * Override to create a filter of the desired type.
     *
     * @param entity Entity to be associated with the new filter.
     * @return New filter.
     */
    IPatientListFilter createFilter(Object entity);

    /**
     * Override to create a filter of the desired type.
     *
     * @param serializedEntity Serialized form of the entity.
     * @return Deserialized filter.
     */
    IPatientListFilter deserializeFilter(String serializedEntity);

    /**
     * Returns the set of capabilities supported by this filter manager.
     *
     * @return A set of FilterCapability elements supported by this filter manager.
     */
    Set<FilterCapability> getCapabilities();

    /**
     * Returns true if the filter manager supports the requested capability.
     *
     * @param capability A FilterCapability element.
     * @return True if the capability is supported by the filter manager.
     */
    boolean hasCapability(FilterCapability capability);

}
