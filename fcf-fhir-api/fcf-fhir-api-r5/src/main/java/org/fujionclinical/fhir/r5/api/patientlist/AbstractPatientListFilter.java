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
package org.fujionclinical.fhir.r5.api.patientlist;

import java.util.Arrays;

/**
 * Represents a filter associated with a patient list. An example of a filter would a clinic
 * location (the entity) used to filter an appointment list.
 */
public abstract class AbstractPatientListFilter implements Comparable<AbstractPatientListFilter> {

    protected static final String DELIM = "|";

    protected static final String REGEX_DELIM = "\\" + DELIM;

    private Object entity;

    private String name;

    public AbstractPatientListFilter(Object entity) {
        this.entity = entity;
        this.name = initName();
    }

    public AbstractPatientListFilter(String value) {
        this.entity = deserialize(value);
        this.name = initName();
    }

    /**
     * Returns the entity object associated with the filter.
     *
     * @return The associated entity object.
     */
    public Object getEntity() {
        return entity;
    }

    /**
     * Sets the entity object associated with the filter.
     *
     * @param entity Entity object.
     */
    protected void setEntity(Object entity) {
        this.entity = entity;
    }

    /**
     * Help method to parse a serialized value. Guarantees the length of the return array.
     *
     * @param value  String value to parse.
     * @param pieces Number of delimited pieces.
     * @return An array of parsed elements.
     */
    protected final String[] parse(
            String value,
            int pieces) {
        String[] result = value == null ? null : value.split(REGEX_DELIM, pieces);
        return result == null ? null : result.length < pieces ? Arrays.copyOf(result, pieces) : result;
    }

    /**
     * Returns an entity instance from its serialized form.
     *
     * @param value Serialized form of entity.
     * @return Deserialized entity instance.
     */
    protected abstract Object deserialize(String value);

    /**
     * Returns the serialized form of the associated entity.
     *
     * @return Serialized form of the entity.
     */
    protected abstract String serialize();

    /**
     * Returns the initial display name for this filter.
     *
     * @return Initial display name.
     */
    protected abstract String initName();

    /**
     * Sets the display name of this filter.
     *
     * @param name The display name.
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the display name of this filter.
     *
     * @return The display name.
     */
    public String getName() {
        return name;
    }

    /**
     * Two filters are considered equal if their associated entities are equal, or, if both entities
     * are null, if their names are equal.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AbstractPatientListFilter)) {
            return false;
        }

        AbstractPatientListFilter filter = (AbstractPatientListFilter) object;

        return entity == filter.entity ? true : entity == null || filter.entity == null ? false : entity
                .equals(filter.entity);
    }

    /**
     * Used to sort filters alphabetically by their name.
     */
    @Override
    public int compareTo(AbstractPatientListFilter filter) {
        return name.compareToIgnoreCase(filter.name);
    }

    /**
     * Returns the serialized form of the filter.
     */
    @Override
    public String toString() {
        return serialize();
    }
}
