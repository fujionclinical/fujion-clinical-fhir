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
package org.fujionclinical.fhir.r4.api.patientlist;

import java.util.List;
import java.util.Set;

public abstract class AbstractPatientListFilterManager implements IPatientListFilterManager {

    private final IPatientList patientList;

    private final Set<FilterCapability> filterCapabilities;

    protected List<AbstractPatientListFilter> filters;

    public AbstractPatientListFilterManager(
            IPatientList patientList,
            Set<FilterCapability> filterCapabilities) {
        this.patientList = patientList;
        this.filterCapabilities = filterCapabilities;
    }

    /**
     * Implement logic to initialize filters.
     *
     * @return The filter list.
     */
    protected abstract List<AbstractPatientListFilter> initFilters();

    /**
     * Override to create a filter of the desired type.
     *
     * @param entity Entity to be associated with the new filter.
     * @return New filter.
     */
    protected abstract AbstractPatientListFilter createFilter(Object entity);

    /**
     * Override to create a filter of the desired type.
     *
     * @param serializedEntity Serialized form of the entity.
     * @return Deserialized filter.
     */
    protected abstract AbstractPatientListFilter deserializeFilter(String serializedEntity);

    /**
     * Implement logic to save filters.
     */
    protected void saveFilters() {
    }

    /**
     * Validates a filter name. Throws an exception if the name is not valid. Override to provide
     * alternate validation logic. Any validation logic should always exclude the ^ and | characters
     * which are used as delimiters when serializing.
     *
     * @param name The filter name to validate.
     */
    protected void validateFilterName(String name) {
        if (getFilterByName(name) != null) {
            throw new PatientListException("The filter named '" + name + "' already exists.");
        }

        if (name == null || name.trim().isEmpty() || !name.matches("^[^\\^\\|]+$")) {
            throw new PatientListException("A name may not contain a '^' or '|' character.");
        }
    }

    /**
     * Force a refresh of the filter list.
     */
    protected void refreshFilters() {
        filters = null;
    }

    /**
     * Returns the filter matching the specified name, or null if not found.
     *
     * @param filterName The filter name.
     * @return The associated filter.
     * @see AbstractPatientListFilterManager#getFilterByName(String)
     */
    protected AbstractPatientListFilter getFilterByName(String filterName) {
        if (filterName != null && initFilters() != null) {
            for (AbstractPatientListFilter filter : filters) {
                if (filter.getName().equals(filterName)) {
                    return filter;
                }
            }
        }

        return null;
    }

    /**
     * Throws an exception if the requested capability is not supported.
     *
     * @param capability Capability to verify.
     */
    private void verifyCapability(FilterCapability capability) {
        if (!hasCapability(capability)) {
            throw new PatientListException("Requested operation is not supported.");
        }
    }

    /**
     * Returns the associated patient list.
     *
     * @return The patient list.
     */
    public IPatientList getPatientList() {
        return patientList;
    }

    /**
     * Adds an entity to the filter list.
     *
     * @param entity Entity to add.
     * @return The filter that was added.
     */
    public AbstractPatientListFilter addEntity(Object entity) {
        AbstractPatientListFilter filter = createFilter(entity);
        initFilters().add(filter);
        saveFilters();
        return filter;
    }

    /* ==================== IPatientListFilterManager ==================== */

    /**
     * Default implementation for adding an entity to the filter list.
     *
     * @see IPatientListFilterManager#addFilter
     */
    @Override
    public AbstractPatientListFilter addFilter(Object entity) {
        verifyCapability(FilterCapability.ADD);
        validateFilterName(entity.toString());
        return addEntity(entity);
    }

    /**
     * Default implementation for removing an entity from the filter list.
     *
     * @see IPatientListFilterManager#removeFilter
     */
    @Override
    public void removeFilter(AbstractPatientListFilter filter) {
        verifyCapability(FilterCapability.REMOVE);

        if (initFilters().remove(filter)) {
            saveFilters();
        }
    }

    /**
     * Default implementation for relocating an entity in the filter list.
     *
     * @see IPatientListFilterManager#moveFilter
     */
    @Override
    public void moveFilter(
            AbstractPatientListFilter filter,
            int index) {
        verifyCapability(FilterCapability.MOVE);

        if (initFilters().remove(filter)) {
            filters.add(index, filter);
            saveFilters();
        }
    }

    /**
     * Default implementation for renaming an entity in the filter list.
     *
     * @see IPatientListFilterManager#renameFilter
     */
    @Override
    public void renameFilter(
            AbstractPatientListFilter filter,
            String newName) {
        verifyCapability(FilterCapability.RENAME);
        validateFilterName(newName);
        initFilters().remove(filter);
        filter.setName(newName);
        initFilters().add(filter);
        saveFilters();
    }

    /**
     * Returns the capabilities of the filter manager.
     *
     * @see IPatientListFilterManager#getCapabilities
     */
    @Override
    public Set<FilterCapability> getCapabilities() {
        return filterCapabilities;
    }

    /**
     * Returns true if the filter manager has the specified capability.
     *
     * @see IPatientListFilterManager#hasCapability(FilterCapability)
     */
    @Override
    public boolean hasCapability(FilterCapability capability) {
        return filterCapabilities != null && filterCapabilities.contains(capability);
    }

}
