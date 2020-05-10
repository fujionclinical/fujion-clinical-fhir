package org.fujionclinical.fhir.api.common.patientlist;

public interface IPatientListFilter extends Comparable<IPatientListFilter> {

    String PIPE = "|";

    String REGEX_DELIM = "\\" + PIPE;

    /**
     * Returns the entity object associated with the filter.
     *
     * @return The associated entity object.
     */
    Object getEntity();

    /**
     * Sets the entity object associated with the filter.
     *
     * @param entity Entity object.
     */
    void setEntity(Object entity);

    /**
     * Help method to parse a serialized value. Guarantees the length of the return array.
     *
     * @param value  String value to parse.
     * @param pieces Number of delimited pieces.
     * @return An array of parsed elements.
     */
    String[] parse(
            String value,
            int pieces);

    /**
     * Returns an entity instance from its serialized form.
     *
     * @param value Serialized form of entity.
     * @return Deserialized entity instance.
     */
    Object deserialize(String value);

    /**
     * Returns the serialized form of the associated entity.
     *
     * @return Serialized form of the entity.
     */
    String serialize();

    /**
     * Returns the initial display name for this filter.
     *
     * @return Initial display name.
     */
    String initName();

    /**
     * Sets the display name of this filter.
     *
     * @param name The display name.
     */
    void setName(String name);

    /**
     * Returns the display name of this filter.
     *
     * @return The display name.
     */
    String getName();

}
