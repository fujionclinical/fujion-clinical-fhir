package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import edu.utah.kmm.model.cool.common.MiscUtils;
import edu.utah.kmm.model.cool.mediator.common.Formatters;
import edu.utah.kmm.model.cool.mediator.fhir.dstu2.transform.PersonNameTransform;
import edu.utah.kmm.model.cool.util.PersonNameParsers;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

import static edu.utah.kmm.model.cool.mediator.common.Formatters.format;

/**
 * FHIR formatting for display.
 */
public class Dstu2Formatters {

    static {
        Formatters.register(AnnotationDt.class, Dstu2Formatters::formatAnnotation);
        Formatters.register(HumanNameDt.class, Dstu2Formatters::formatName);
        Formatters.register(CodeableConceptDt.class, Dstu2Formatters::formatCodeableConcept);
        Formatters.register(DateTimeDt.class, Dstu2Formatters::formatDateTime);
        Formatters.register(DateDt.class, Dstu2Formatters::formatDate);
        Formatters.register(PeriodDt.class, Dstu2Formatters::formatPeriod);
        Formatters.register(QuantityDt.class, Dstu2Formatters::formatQuantity);
        Formatters.register(ResourceReferenceDt.class, Dstu2Formatters::formatReference);
        Formatters.register(SimpleQuantityDt.class, Dstu2Formatters::formatSimpleQuantity);
        Formatters.register(TimingDt.class, Dstu2Formatters::formatTiming);
        Formatters.register(AgeDt.class, Dstu2Formatters::formatAge);
        Formatters.register(Location.class, Dstu2Formatters::formatLocation);
        Formatters.register(IdentifierDt.class, Dstu2Formatters::formatIdentifier);
    }

    /**
     * Returns the displayable value for an annotation.
     *
     * @param value The annotation value.
     * @return The displayable value (possibly null).
     */
    public static String formatAnnotation(AnnotationDt value) {
        return value.getText();
    }

    /**
     * Returns a displayable value for a codeable concept.
     *
     * @param value Codeable concept.
     * @return Displayable value.
     */
    public static String formatCodeableConcept(CodeableConceptDt value) {
        String text = value.getText();

        if (!StringUtils.isEmpty(text)) {
            return text;
        }

        CodingDt coding = MiscUtils.getFirst(value.getCoding());
        return coding == null ? null : !coding.getDisplayElement().isEmpty() ? coding.getDisplay() : coding.getCode();
    }

    /**
     * Returns the displayable value for an timestamp.
     *
     * @param value The timestamp.
     * @return The displayable value (possibly null).
     */
    public static String formatDateTime(DateTimeDt value) {
        return format(value.getValue());
    }

    /**
     * Returns the displayable value for a date.
     *
     * @param value The date value.
     * @return The displayable value (possibly null).
     */
    public static String formatDate(DateDt value) {
        return format(value.getValue());
    }

    /**
     * Returns the displayable value for period.
     *
     * @param value The period value.
     * @return The displayable value (possibly null).
     */
    public static String formatPeriod(PeriodDt value) {
        Date start = value.getStart();
        Date end = value.getEnd();
        String result = "";

        if (start != null) {
            result = format(start);

            if (start.equals(end)) {
                end = null;
            }
        }

        if (end != null) {
            result += (result.isEmpty() ? "" : " - ") + format(end);
        }

        return result;
    }

    /**
     * Returns the displayable value for a quantity.
     *
     * @param value The quantity value.
     * @return The displayable value (possibly null).
     */
    public static String formatQuantity(QuantityDt value) {
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
    public static String formatReference(ResourceReferenceDt value) {
        return value == null ? null : value.getDisplay().isEmpty() ? null : value.getDisplay().getValue();
    }

    /**
     * Returns the displayable value for a simple quantity.
     *
     * @param value The quantity value.
     * @return The displayable value (possibly null).
     */
    public static String formatSimpleQuantity(SimpleQuantityDt value) {
        String unit = StringUtils.trimToNull(value.getUnit());
        return value.getValue().toPlainString() + (unit == null ? "" : " " + unit);
    }

    /**
     * Returns the displayable value for a timing.
     *
     * @param value The timing value.
     * @return The displayable value (possibly null).
     */
    public static String formatTiming(TimingDt value) {
        StringBuilder sb = new StringBuilder();
        String code = format(value.getCode(), null);

        if (code != null) {
            sb.append(code).append(" ");
        }

        TimingDt.Repeat repeat = !value.getRepeat().isEmpty() ? value.getRepeat() : null;

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
    public static String formatAge(AgeDt value) {
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
    public static String formatIdentifier(IdentifierDt value) {
        return value == null ? null : value.getValue();
    }

    public static String formatName(HumanNameDt name) {
        return PersonNameParsers.get().toString(PersonNameTransform.getInstance().toLogicalModel(name));
    }

    /**
     * Enforce static class.
     */
    private Dstu2Formatters() {
    }

}
