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
package org.fujionclinical.fhir.api.dstu2.common;

import edu.utah.kmm.model.cool.common.MiscUtils;
import edu.utah.kmm.model.cool.mediator.common.Formatters;
import edu.utah.kmm.model.cool.mediator.fhir.dstu2.person.PersonNameTransform;
import edu.utah.kmm.model.cool.util.PersonNameParsers;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu2.model.*;

import java.math.BigDecimal;
import java.util.Date;

import static edu.utah.kmm.model.cool.mediator.common.Formatters.format;

/**
 * FHIR formatting for display.
 */
public class Dstu2Formatters {

    static {
        Formatters.register(Annotation.class, Dstu2Formatters::formatAnnotation);
        Formatters.register(HumanName.class, Dstu2Formatters::formatName);
        Formatters.register(CodeableConcept.class, Dstu2Formatters::formatCodeableConcept);
        Formatters.register(DateTimeType.class, Dstu2Formatters::formatDateTime);
        Formatters.register(DateType.class, Dstu2Formatters::formatDate);
        Formatters.register(Period.class, Dstu2Formatters::formatPeriod);
        Formatters.register(Quantity.class, Dstu2Formatters::formatQuantity);
        Formatters.register(Reference.class, Dstu2Formatters::formatReference);
        Formatters.register(SimpleQuantity.class, Dstu2Formatters::formatSimpleQuantity);
        Formatters.register(Timing.class, Dstu2Formatters::formatTiming);
        Formatters.register(Age.class, Dstu2Formatters::formatAge);
        Formatters.register(Location.class, Dstu2Formatters::formatLocation);
        Formatters.register(Identifier.class, Dstu2Formatters::formatIdentifier);
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
        return format(value.getValue());
    }

    /**
     * Returns the displayable value for a date.
     *
     * @param value The date value.
     * @return The displayable value (possibly null).
     */
    public static String formatDate(DateType value) {
        return format(value.getValue());
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
    private Dstu2Formatters() {
    }

}
