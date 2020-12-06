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
package org.fujionclinical.fhir.api.r5.common;

import edu.utah.kmm.model.cool.common.MiscUtils;
import edu.utah.kmm.model.cool.mediator.common.MimeContent;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.*;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

import static edu.utah.kmm.model.cool.mediator.fhir.common.FhirUtils.stripVersion;
import static edu.utah.kmm.model.cool.mediator.fhir.r5.common.R5Utils.getMRN;
import static edu.utah.kmm.model.cool.mediator.fhir.r5.common.R5Utils.getResourceType;
import static org.fujionclinical.fhir.api.common.core.FhirUtil.getListProperty;

public class R5Util {

    /**
     * Performs an equality check on two references using their id's.
     *
     * @param <T>  Resource type.
     * @param ref1 The first resource.
     * @param ref2 The second resource.
     * @return True if the two references have equal id's.
     */
    public static <T extends Reference> boolean areEqual(
            T ref1,
            T ref2) {
        return areEqual(ref1, ref2, false);
    }

    /**
     * Performs an equality check on two resources using their id's.
     *
     * @param <T>           Resource type.
     * @param ref1          The first resource.
     * @param ref2          The second resource.
     * @param ignoreVersion If true, ignore any version qualifiers in the comparison.
     * @return True if the two resources have equal id's.
     */
    public static <T extends Reference> boolean areEqual(
            T ref1,
            T ref2,
            boolean ignoreVersion) {
        if (ref1 == null || ref2 == null) {
            return false;
        }

        return ref1 == ref2 || getIdAsString(ref1, ignoreVersion).equals(getIdAsString(ref2, ignoreVersion));
    }

    /**
     * Performs an equality check between a resource and a reference using their id's.
     *
     * @param <T> The Resource type.
     * @param <R> The reference type.
     * @param res The resource.
     * @param ref The reference.
     * @return True if the two inputs have equal id's.
     */
    public static <T extends IBaseResource, R extends Reference> boolean areEqual(
            T res,
            R ref) {
        return areEqual(res, ref, false);
    }

    /**
     * Performs an equality check between a resource and a reference using their id's.
     *
     * @param <T>           Resource type.
     * @param <R>           Reference type.
     * @param res           The resource.
     * @param ref           The reference.
     * @param ignoreVersion If true, ignore any version qualifiers in the comparison.
     * @return True if the two inputs have equal id's.
     */
    public static <T extends IBaseResource, R extends Reference> boolean areEqual(
            T res,
            R ref,
            boolean ignoreVersion) {
        if (res == null || ref == null) {
            return false;
        }

        IBaseResource res2 = ref.getResource();

        if (res2 != null) {
            return FhirUtil.areEqual(res, res2, ignoreVersion);
        }

        return getIdAsString(ref, ignoreVersion).equals(FhirUtil.getIdAsString(res, ignoreVersion));
    }

    /**
     * Method returns true if two quantities are equal. Compares two quantities by comparing their
     * values and their units. TODO Do a comparator instead
     *
     * @param qty1 The first quantity
     * @param qty2 The second quantity
     * @return True if the two quantities are equal.
     */
    public static boolean equalQuantities(
            Quantity qty1,
            Quantity qty2) {
        if (qty1 == null || qty2 == null || qty1.getUnit() == null || qty2.getUnit() == null || qty1.getValue() == null
                || qty2.getValue() == null) {
            return false;
        } else if (qty1 == qty2) {
            return true;
        } else if ((qty1.getValue().compareTo(qty2.getValue()) == 0) && qty1.getUnit().equals(qty2.getUnit())) {
            // TODO: Fix floating compares within some margin.
            return true;
        } else {
            // TODO Flawed because no unit conversion done here. I am leaving this for a good utility.
            return false;
        }
    }

    /**
     * Convert a string-based time unit to the corresponding enum.
     *
     * @param timeUnit The time unit.
     * @return The corresponding enumeration value.
     */
    public static Timing.UnitsOfTime convertTimeUnitToEnum(String timeUnit) {
        Timing.UnitsOfTime value = MiscUtils.stringToEnum(timeUnit, Timing.UnitsOfTime.class);
        Assert.notNull(value, () -> "Unknown time unit " + timeUnit);
        return value;
    }

    /**
     * Convenience method that creates a CodeableConcept with a single coding.
     *
     * @param system      The coding system.
     * @param code        The code.
     * @param displayName The concept's display name.
     * @return A CodeableConcept instance.
     */
    public static CodeableConcept createCodeableConcept(
            String system,
            String code,
            String displayName) {
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding(system, code, displayName);
        codeableConcept.addCoding(coding);
        return codeableConcept;
    }

    /**
     * Creates a period object from a start and end date.
     *
     * @param startDate The starting date.
     * @param endDate   The ending date.
     * @return A period object, or null if both dates are null.
     */
    public static Period createPeriod(
            Date startDate,
            Date endDate) {
        Period period = null;

        if (startDate != null) {
            period = new Period();
            period.setStart(startDate);
        }

        if (endDate != null) {
            (period == null ? period = new Period() : period).setEnd(endDate);
        }

        return period;
    }

    /**
     * Returns an address of the desired use category from a list.
     *
     * @param list List of addresses to consider.
     * @param uses One or more use categories. These are searched in order until one is found. A
     *             null value matches any use category.
     * @return An address with a matching use category, or null if none found.
     */
    public static Address getAddress(
            List<Address> list,
            Address.AddressUse... uses) {
        for (Address.AddressUse use : uses) {
            for (Address address : list) {
                if (use == null || use.equals(address.getUseElement().getValue())) {
                    return address;
                }
            }
        }

        return null;
    }

    /**
     * Returns a list of addresses from a resource if one exists.
     *
     * @param resource Resource of interest.
     * @return List of addresses associated with resource or null if none.
     */
    public static List<Address> getAddresses(IBaseResource resource) {
        return getListProperty(resource, "address");
    }

    /**
     * Returns identifiers for the given resource, if any.
     *
     * @param resource Resource whose identifiers are sought.
     * @return List of associated identifier, or null if the resource doesn't support identifiers.
     */
    @SuppressWarnings("unchecked")
    public static List<Identifier> getIdentifiers(IBaseResource resource) {
        return getProperty(resource, "getIdentifier", List.class);
    }

    /**
     * Returns a patient's MRN. (What labels should be explicitly considered?)
     *
     * @param patient Patient
     * @return MRN as a string.
     */
    public static String getMRNString(Patient patient) {
        Identifier identifier = getMRN(patient);
        return identifier == null ? "" : identifier.getValue();
    }

    /**
     * Returns the string representation of the reference's resource id.
     *
     * @param reference    The reference.
     * @param stripVersion If true and the id has a version qualifier, remove it.
     * @return The string representation of the id.
     */
    public static String getIdAsString(
            Reference reference,
            boolean stripVersion) {
        IBaseResource res = reference == null ? null : reference.getResource();

        if (res != null) {
            return FhirUtil.getIdAsString(res, stripVersion);
        }
        String result = reference == null ? null : reference.getReference();
        return result == null ? "" : stripVersion ? stripVersion(result) : result;
    }

    /**
     * Returns the first identifier from the list that matches one of the specified system.
     *
     * @param list   List of identifiers to consider.
     * @param system The identifier system to be matched.
     * @return A matching identifier, or null if not found.
     */
    public static Identifier getIdentifierBySystem(
            List<Identifier> list,
            String system) {
        for (Identifier id : list) {
            if (system.equals(id.getSystem())) {
                return id;
            }
        }

        return null;
    }

    /**
     * Returns the official or usual name if found, otherwise returns the first name found.
     *
     * @param list List of names to consider.
     * @return A name with a matching use category, or null if none found.
     */
    public static HumanName getName(List<HumanName> list) {
        return getName(list, HumanName.NameUse.OFFICIAL, HumanName.NameUse.USUAL, null);
    }

    /**
     * Returns the patient associated with a resource.
     *
     * @param resource Resource whose associated patient is sought.
     * @return A patient resource.
     */
    public static Reference getPatient(IBaseResource resource) {
        Reference ref = getProperty(resource, "getPatient", Reference.class);
        ref = ref != null ? ref : getProperty(resource, "getSubject", Reference.class);
        return ref == null || ref.getReference().isEmpty() ? null
                : Patient.class.equals(getResourceType(ref.getReferenceElement())) ? ref : null;
    }

    /**
     * Returns a name of the desired use category from a list.
     *
     * @param list List of names to consider.
     * @param uses One or more use categories. These are searched in order until one is found. A
     *             null value matches any use category.
     * @return A name with a matching use category, or null if none found.
     */
    public static HumanName getName(
            List<HumanName> list,
            HumanName.NameUse... uses) {
        for (HumanName.NameUse use : uses) {
            for (HumanName name : list) {
                if (use == null || use.equals(name.getUseElement().getValue())) {
                    return name;
                }
            }
        }

        return null;
    }

    /**
     * Returns a list of names from a resource if one exists.
     *
     * @param resource Resource of interest.
     * @return List of names associated with resource or null if none.
     */
    public static List<HumanName> getNames(IBaseResource resource) {
        return getListProperty(resource, "name");
    }

    /**
     * Returns the MimeContent of an attachment.
     *
     * @param attachment An attachment.
     * @return The MimeContent of the attachment.
     */
    public static MimeContent getContent(Attachment attachment) {
        String contentType = attachment.getContentType();

        if (!attachment.getDataElement().isEmpty()) {
            return new MimeContent(contentType, attachment.getData());
        } else if (!attachment.getUrlElement().isEmpty()) {
            return new MimeContent(contentType, attachment.getUrl());
        } else {
            return null;
        }
    }

    /**
     * Returns a coding of the desired system from a list.
     *
     * @param list    List of codings to consider.
     * @param systems One or more systems to consider. These are searched in order until one is
     *                found. A null value matches any system.
     * @return An coding with a matching system, or null if none found.
     */
    public static Coding getCoding(
            List<Coding> list,
            String... systems) {
        for (String system : systems) {
            for (Coding coding : list) {
                if (system == null || system.equals(coding.getSystem())) {
                    return coding;
                }
            }
        }

        return null;
    }

    /**
     * Returns an contact of the desired type category from a list.
     *
     * @param list List of contacts to consider.
     * @param type Contact type to find (e.g., "home:phone").
     * @return A contact with a matching type, or null if none found.
     */
    public static ContactPoint getContact(
            List<ContactPoint> list,
            String type) {
        String[] pcs = type.split(":", 2);

        for (ContactPoint contact : list) {
            if (pcs[0].equals(contact.getUse()) && pcs[1].equals(contact.getSystem())) {
                return contact;
            }
        }

        return null;
    }

    /**
     * Returns the value of a property from a resource base.
     *
     * @param <T>           The property value type.
     * @param object        The resource containing the property.
     * @param getter        The name of the getter method for the property.
     * @param expectedClass The expected class of the property value (null for any).
     * @return The value of the property. A null return value may mean the property does not exist
     *         or the property getter returned null. Will never throw an exception.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(
            Object object,
            String getter,
            Class<T> expectedClass) {
        Object result = null;

        try {
            result = MethodUtils.invokeMethod(object, getter, (Object[]) null);
            result = result == null || expectedClass == null ? result : expectedClass.isInstance(result) ? result : null;
        } catch (Exception e) {
            // NOP
        }

        return (T) result;
    }

    /**
     * Method sets the FHIR repeat for the given frequency code
     *
     * @param frequencyCode The frequency code.
     * @return The corresponding timing.
     */
    public static Timing.TimingRepeatComponent getRepeatFromFrequencyCode(String frequencyCode) {
        Timing.TimingRepeatComponent repeat = new Timing.TimingRepeatComponent();
        if (frequencyCode != null && frequencyCode.equals("QD")) {
            repeat.setFrequency(1);
            repeat.setPeriod(25);
        }
        if (frequencyCode != null && frequencyCode.equals("Q8H")) {
            repeat.setFrequency(1);
            repeat.setPeriod(8);
        }
        return repeat;
    }

    private R5Util() {
    }

}
