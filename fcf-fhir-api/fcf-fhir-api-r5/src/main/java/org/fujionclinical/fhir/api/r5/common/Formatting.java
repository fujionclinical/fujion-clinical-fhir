package org.fujionclinical.fhir.api.r5.common;

import ca.uhn.fhir.util.DateUtils;
import edu.utah.kmm.model.cool.common.MiscUtils;
import edu.utah.kmm.model.cool.mediator.common.Formatters;
import edu.utah.kmm.model.cool.mediator.fhir.r5.transform.PersonNameTransform;
import edu.utah.kmm.model.cool.util.PersonNameParsers;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r5.model.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * FHIR formatting for display.
 */
public class Formatting {

    static {
        Formatters.register(Annotation.class, Formatting::formatAnnotation);
        Formatters.register(HumanName.class, Formatting::formatName);
        Formatters.register(CodeableConcept.class, Formatting::formatCodeableConcept);
        Formatters.register(DateTimeType.class, Formatting::formatDateTime);
        Formatters.register(DateType.class, Formatting::formatDate);
        Formatters.register(Period.class, Formatting::formatPeriod);
        Formatters.register(Quantity.class, Formatting::formatQuantity);
        Formatters.register(Reference.class, Formatting::formatReference);
        Formatters.register(SimpleQuantity.class, Formatting::formatSimpleQuantity);
        Formatters.register(Timing.class, Formatting::formatTiming);
        Formatters.register(Age.class, Formatting::formatAge);
        Formatters.register(Location.class, Formatting::formatLocation);
        Formatters.register(Identifier.class, Formatting::formatIdentifier);
    }

    public static String format(
            Object o,
            String deflt) {
        return Formatters.format(o, deflt);
    }

    public static String format(Object o) {
        return Formatters.format(o);
    }

    /**
     * Returns the displayable value for an annotation.
     *
     * @param value The annotation value.
     * @return The displayable value (possibly null).
     */
    public static String formatAnnotation(Annotation value) {
        return value.getText();
    }

    /**
     * Returns a displayable value for a codeable concept.
     *
     * @param value Codeable concept.
     * @return Displayable value.
     */
    public static String formatCodeableConcept(CodeableConcept value) {
        String text = value.getText();

        if (!StringUtils.isEmpty(text)) {
            return text;
        }

        Coding coding = MiscUtils.getFirst(value.getCoding());
        return coding == null ? null : !coding.getDisplayElement().isEmpty() ? coding.getDisplay() : coding.getCode();
    }

    /**
     * Returns the displayable value for an timestamp.
     *
     * @param value The timestamp.
     * @return The displayable value (possibly null).
     */
    public static String formatDateTime(DateTimeType value) {
        return DateUtils.formatDate(value.getValue());
    }

    /**
     * Returns the displayable value for a date.
     *
     * @param value The date value.
     * @return The displayable value (possibly null).
     */
    public static String formatDate(DateType value) {
        return DateUtils.formatDate(value.getValue());
    }

    /**
     * Returns the displayable value for period.
     *
     * @param value The period value.
     * @return The displayable value (possibly null).
     */
    public static String formatPeriod(Period value) {
        Date start = value.getStart();
        Date end = value.getEnd();
        String result = "";

        if (start != null) {
            result = DateUtils.formatDate(start);

            if (start.equals(end)) {
                end = null;
            }
        }

        if (end != null) {
            result += (result.isEmpty() ? "" : " - ") + DateUtils.formatDate(end);
        }

        return result;
    }

    /**
     * Returns the displayable value for a quantity.
     *
     * @param value The quantity value.
     * @return The displayable value (possibly null).
     */
    public static String formatQuantity(Quantity value) {
        BigDecimal rawValue = value.getValue();
        String val = rawValue == null ? "" : rawValue.toString();
        String units = val.isEmpty() ? null : StringUtils.trimToNull(value.getUnit());
        return val + (units == null ? "" : " " + units);
    }

    /**
     * Returns the displayable value for a reference.
     *
     * @param value The reference value.
     * @return The displayable value (possibly null).
     */
    public static String formatReference(Reference value) {
        return value == null ? null : value.getDisplay().isEmpty() ? null : value.getDisplay();
    }

    /**
     * Returns the displayable value for a simple quantity.
     *
     * @param value The quantity value.
     * @return The displayable value (possibly null).
     */
    public static String formatSimpleQuantity(SimpleQuantity value) {
        String unit = StringUtils.trimToNull(value.getUnit());
        return value.getValue().toPlainString() + (unit == null ? "" : " " + unit);
    }

    /**
     * Returns the displayable value for a timing.
     *
     * @param value The timing value.
     * @return The displayable value (possibly null).
     */
    public static String formatTiming(Timing value) {
        StringBuilder sb = new StringBuilder();
        String code = format(value.getCode(), null);

        if (code != null) {
            sb.append(code).append(" ");
        }

        Timing.TimingRepeatComponent repeat = !value.getRepeat().isEmpty() ? value.getRepeat() : null;

        if (repeat != null) {
            // TODO: finish
        }

        if (!value.getEvent().isEmpty()) {
            String events = format(value.getEvent());

            if (!events.isEmpty()) {
                sb.append(" at ").append(events);
            }
        }

        return sb.length() == 0 ? null : sb.toString();
    }

    /**
     * Returns the displayable value for an age.
     *
     * @param value The age value.
     * @return The displayable value (possibly null).
     */
    public static String formatAge(Age value) {
        String unit = value.getUnit().isEmpty() ? "" : " " + value.getUnit();
        BigDecimal age = value.getValue();
        return age == null ? null : age.toString() + unit;
    }

    /**
     * Returns the displayable value for a location.
     *
     * @param value The location.
     * @return The displayable value (possibly null).
     */
    public static String formatLocation(Location value) {
        if (value.getName() != null) {
            return value.getName();
        }

        return format(value.getIdentifier());
    }

    /**
     * Returns the displayable value for an identifier.
     *
     * @param value The identifier.
     * @return The displayable value (possibly null).
     */
    public static String formatIdentifier(Identifier value) {
        return value == null ? null : value.getValue();
    }

    public static String formatName(HumanName name) {
        return PersonNameParsers.get().toString(PersonNameTransform.getInstance().toLogicalModel(name));
    }

    /**
     * Enforce static class.
     */
    private Formatting() {
    }

}
