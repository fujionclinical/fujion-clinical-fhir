/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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
package org.fujionclinical.fhir.r4.api.common;

import ca.uhn.fhir.rest.api.MethodOutcome;
import org.fujion.common.DateUtil;
import org.fujionclinical.fhir.r4.api.terminology.Constants;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.r4.model.Timing.TimingRepeatComponent;
import org.hl7.fhir.r4.model.Timing.UnitsOfTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Domain object utility methods.
 */
public class FhirUtil extends org.fujionclinical.fhir.api.common.core.FhirUtil {
    
    public static class OperationOutcomeException extends RuntimeException {
        
        private static final long serialVersionUID = 1L;
        
        private final OperationOutcome operationOutcome;
        
        private final IssueSeverity severity;
        
        private OperationOutcomeException(String message, IssueSeverity severity, OperationOutcome operationOutcome) {
            super(message);
            this.severity = severity;
            this.operationOutcome = operationOutcome;
        }
        
        public OperationOutcome getOperationOutcome() {
            return operationOutcome;
        }
        
        public IssueSeverity getSeverity() {
            return severity;
        }
    }
    
    public static IHumanNameParser defaultHumanNameParser = new HumanNameParser();
    
    /**
     * Performs an equality check on two references using their id's.
     *
     * @param <T> Resource type.
     * @param ref1 The first resource.
     * @param ref2 The second resource.
     * @return True if the two references have equal id's.
     */
    public static <T extends Reference> boolean areEqual(T ref1, T ref2) {
        return areEqual(ref1, ref2, false);
    }
    
    /**
     * Performs an equality check on two resources using their id's.
     *
     * @param <T> Resource type.
     * @param ref1 The first resource.
     * @param ref2 The second resource.
     * @param ignoreVersion If true, ignore any version qualifiers in the comparison.
     * @return True if the two resources have equal id's.
     */
    public static <T extends Reference> boolean areEqual(T ref1, T ref2, boolean ignoreVersion) {
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
    public static <T extends IBaseResource, R extends Reference> boolean areEqual(T res, R ref) {
        return areEqual(res, ref, false);
    }
    
    /**
     * Performs an equality check between a resource and a reference using their id's.
     *
     * @param <T> Resource type.
     * @param <R> Reference type.
     * @param res The resource.
     * @param ref The reference.
     * @param ignoreVersion If true, ignore any version qualifiers in the comparison.
     * @return True if the two inputs have equal id's.
     */
    public static <T extends IBaseResource, R extends Reference> boolean areEqual(T res, R ref, boolean ignoreVersion) {
        if (res == null || ref == null) {
            return false;
        }
        
        IBaseResource res2 = ref.getResource();
        
        if (res2 != null) {
            return areEqual(res, res2, ignoreVersion);
        }
        
        return getIdAsString(ref, ignoreVersion).equals(getIdAsString(res, ignoreVersion));
    }
    
    /**
     * Checks the response from a server request to determine if it is an OperationOutcome with a
     * severity of ERROR or FATAL. If so, it will throw a runtime exception with the diagnostics of
     * the issue(s).
     *
     * @param resource The resource returned by a server request.
     * @return Returns true if the resource was an OperationOutcome with no critical issues.
     * @throws OperationOutcomeException Exception if severity was ERROR or FATAL.
     */
    public static boolean checkOutcome(IBaseResource resource) {
        if (resource instanceof OperationOutcome) {
            OperationOutcome outcome = (OperationOutcome) resource;
            IssueSeverity severity = IssueSeverity.NULL;
            
            if (outcome.hasIssue()) {
                StringBuilder sb = new StringBuilder();
                
                for (OperationOutcomeIssueComponent issue : outcome.getIssue()) {
                    IssueSeverity theSeverity = issue.getSeverity();
                    
                    if (theSeverity == IssueSeverity.ERROR || theSeverity == IssueSeverity.FATAL) {
                        sb.append(issue.getDiagnostics()).append(" (").append(theSeverity.getDisplay()).append(")\n");
                        severity = theSeverity.ordinal() < severity.ordinal() ? theSeverity : severity;
                    }
                }
                
                if (sb.length() != 0) {
                    throw new OperationOutcomeException(sb.toString(), severity, outcome);
                }
                
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Convert a string-based time unit to the corresponding enum.
     *
     * @param timeUnit The time unit.
     * @return The corresponding enumeration value.
     */
    public static UnitsOfTime convertTimeUnitToEnum(String timeUnit) {
        try {
            return UnitsOfTime.valueOf(timeUnit.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown time unit " + timeUnit);
        }
    }
    
    /**
     * Convenience method that creates a CodeableConcept with a single coding.
     *
     * @param system The coding system.
     * @param code The code.
     * @param displayName The concept's display name.
     * @return A CodeableConcept instance.
     */
    public static CodeableConcept createCodeableConcept(String system, String code, String displayName) {
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding(system, code, displayName);
        codeableConcept.addCoding(coding);
        return codeableConcept;
    }
    
    /**
     * Convenience method that creates an Identifier with the specified system and value.
     *
     * @param system The system.
     * @param value The value.
     * @return An identifier.
     */
    public static Identifier createIdentifier(String system, String value) {
        Identifier identifier = new Identifier();
        identifier.setSystem(system);
        identifier.setValue(value);
        return identifier;
    }
    
    /**
     * Creates a period object from a start and end date.
     *
     * @param startDate The starting date.
     * @param endDate The ending date.
     * @return A period object, or null if both dates are null.
     */
    public static Period createPeriod(Date startDate, Date endDate) {
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
     * Method returns true if two quantities are equal. Compares two quantities by comparing their
     * values and their units. TODO Do a comparator instead
     *
     * @param qty1 The first quantity
     * @param qty2 The second quantity
     * @return True if the two quantities are equal.
     */
    public static boolean equalQuantities(Quantity qty1, Quantity qty2) {
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
     * Formats a name using the active parser.
     *
     * @param name HumanName instance.
     * @return Formatted name.
     */
    public static String formatName(HumanName name) {
        return name == null ? "" : defaultHumanNameParser.toString(name);
    }
    
    /**
     * Format the "usual" name.
     *
     * @param names List of names
     * @return A formatted name.
     */
    public static String formatName(List<HumanName> names) {
        return formatName(names, NameUse.OFFICIAL, NameUse.USUAL, null);
    }
    
    /**
     * Format a name of the specified use category.
     *
     * @param names List of names
     * @param uses Use categories (use categories to search).
     * @return A formatted name.
     */
    public static String formatName(List<HumanName> names, NameUse... uses) {
        return formatName(getName(names, uses));
    }
    
    /**
     * Returns an address of the desired use category from a list.
     *
     * @param list List of addresses to consider.
     * @param uses One or more use categories. These are searched in order until one is found. A
     *            null value matches any use category.
     * @return An address with a matching use category, or null if none found.
     */
    public static Address getAddress(List<Address> list, AddressUse... uses) {
        for (AddressUse use : uses) {
            for (Address address : list) {
                if (use == null || use.equals(address.getUse())) {
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
        return getListProperty(resource, "address", Address.class);
    }
    
    /**
     * Returns a coding of the desired system from a list.
     *
     * @param list List of codings to consider.
     * @param systems One or more systems to consider. These are searched in order until one is
     *            found. A null value matches any system.
     * @return An coding with a matching system, or null if none found.
     */
    public static Coding getCoding(List<Coding> list, String... systems) {
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
    public static ContactPoint getContact(List<ContactPoint> list, String type) {
        String[] pcs = type.split(":", 2);

        for (ContactPoint contact : list) {
            ContactPoint.ContactPointSystem system = contact.getSystem();
            ContactPoint.ContactPointUse use = contact.getUse();

            if (pcs[0].equals(use == null ? null : use.toCode()) && pcs[1].equals(system == null ? null : system.toCode())) {
                return contact;
            }
        }

        return null;
    }
    
    public static String getDisplayValue(Annotation value) {
        return value.getText();
    }
    
    /**
     * Returns a displayable value for a codeable concept.
     *
     * @param value Codeable concept.
     * @return Displayable value.
     */
    public static String getDisplayValue(CodeableConcept value) {
        Coding coding = FhirUtil.getFirst(value.getCoding());
        String result = coding == null ? "" : coding.getDisplay();
        return result == null ? coding.getCode() : result;
    }
    
    public static String getDisplayValue(DateTimeType value) {
        return DateUtil.formatDate(value.getValue());
    }
    
    public static String getDisplayValue(DateType value) {
        return DateUtil.formatDate(value.getValue());
    }
    
    public static String getDisplayValue(Period value) {
        Date start = value.getStart();
        Date end = value.getEnd();
        String result = "";
        
        if (start != null) {
            result = DateUtil.formatDate(start);
            
            if (start.equals(end)) {
                end = null;
            }
        }
        
        if (end != null) {
            result += (result.isEmpty() ? "" : " - ") + DateUtil.formatDate(end);
        }
        
        return result;
    }
    
    public static String getDisplayValue(Quantity value) {
        String val = value.hasValue() ? value.getValue().toString() : "";
        String units = val.isEmpty() || !value.hasUnit() ? "" : (" " + value.getUnit());
        return val + units;
    }
    
    public static String getDisplayValue(Reference value) {
        return value.getDisplay();
    }
    
    public static String getDisplayValue(SimpleQuantity value) {
        String unit = value.hasUnit() ? " " + value.getUnit() : "";
        return value.getValue().toPlainString() + unit;
    }
    
    public static String getDisplayValue(Timing value) {
        StringBuilder sb = new StringBuilder(getDisplayValueForType(value.getCode())).append(" ");
        TimingRepeatComponent repeat = value.getRepeat();
        
        if (!repeat.isEmpty()) {
            // TODO: finish
        }
        
        if (!value.getEvent().isEmpty()) {
            sb.append(" at ").append(getDisplayValueForTypes(value.getEvent(), ", "));
        }
        
        return sb.toString();
    }
    
    public static String getDisplayValue(Age value) {
        String unit = value.hasUnit() ? " " + value.getUnit() : "";
        BigDecimal age = value.getValue();
        return age == null ? "" : age.toString() + unit;
    }
    
    /**
     * Extracts resources of the specified class from a bundle.
     *
     * @param <T> Resource type.
     * @param bundle The bundle.
     * @param clazz Class of resource to extract.
     * @return The list of extracted resources.
     */
    @SuppressWarnings("unchecked")
    public static <T extends IBaseResource> List<T> getEntries(Bundle bundle, Class<T> clazz) {
        return (List<T>) getEntries(bundle, Collections.singletonList(clazz), null);
    }
    
    /**
     * Extracts resources from a bundle according to the inclusion and exclusion criteria.
     *
     * @param <T> Resource type.
     * @param bundle The bundle.
     * @param inclusions List of resource classes to extract. May be null to indicate all classes
     *            should be extracted.
     * @param exclusions List of resource classes to be excluded. May be null to indicate no classes
     *            should be excluded. Exclusions take precedence over inclusions.
     * @return The list of extracted resources.
     */
    public static <T extends IBaseResource> List<IBaseResource> getEntries(Bundle bundle, List<Class<T>> inclusions,
                                                                           List<Class<T>> exclusions) {
        List<IBaseResource> entries = new ArrayList<>();
        
        if (bundle != null) {
            for (BundleEntryComponent entry : bundle.getEntry()) {
                IBaseResource resource = entry.getResource();
                boolean exclude = exclusions != null && classMatches(exclusions, resource);
                boolean include = !exclude && (inclusions == null || classMatches(inclusions, resource));
                
                if (include) {
                    entries.add(resource);
                }
            }
        }
        
        return entries;
    }
    
    /**
     * Returns the string representation of the reference's resource id.
     *
     * @param reference The reference.
     * @param stripVersion If true and the id has a version qualifier, remove it.
     * @return The string representation of the id.
     */
    public static String getIdAsString(Reference reference, boolean stripVersion) {
        IBaseResource res = reference == null ? null : reference.getResource();
        
        if (res != null) {
            return getIdAsString(res, stripVersion);
        }
        String result = reference == null ? null : reference.getReference();
        return result == null ? "" : stripVersion ? stripVersion(result) : result;
    }
    
    /**
     * Returns the first identifier from the list that matches one of the specified types. A search
     * is performed for each specified type, returning when a match is found.
     *
     * @param list List of identifiers to consider.
     * @param types Coding types to be matched.
     * @return A matching identifier, or null if not found.
     */
    public static Identifier getIdentifier(List<Identifier> list, Coding... types) {
        for (Coding type : types) {
            for (Identifier id : list) {
                for (Coding coding : id.getType().getCoding()) {
                    if (coding.getSystem().equals(type.getSystem()) && coding.getCode().equals(type.getCode())) {
                        return id;
                    }
                }
            }
        }
        
        return null;
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
     * Returns a patient's MRN. (What types should be explicitly considered?)
     *
     * @param patient Patient
     * @return MRN identifier
     */
    public static Identifier getMRN(Patient patient) {
        return patient == null ? null : getIdentifier(patient.getIdentifier(), Constants.CODING_MRN);
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
     * Returns the official or usual name if found, otherwise returns the first name found.
     *
     * @param list List of names to consider.
     * @return A name with a matching use category, or null if none found.
     */
    public static HumanName getName(List<HumanName> list) {
        return getName(list, NameUse.OFFICIAL, NameUse.USUAL, null);
    }

    /**
     * Returns a name of the desired use category from a list.
     *
     * @param list List of names to consider.
     * @param uses One or more use categories. These are searched in order until one is found. A
     *            null value matches any use category.
     * @return A name with a matching use category, or null if none found.
     */
    public static HumanName getName(List<HumanName> list, NameUse... uses) {
        for (NameUse use : uses) {
            for (HumanName name : list) {
                if (use == null || use.equals(name.getUse())) {
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
        return getListProperty(resource, "name", HumanName.class);
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
        return ref == null || !ref.hasReference() ? null
                : "Patient".equals(getResourceType(ref.getReferenceElement())) ? ref : null;
    }
    
    /**
     * Method sets the FHIR repeat for the given frequency code
     *
     * @param frequencyCode The frequency code.
     * @return The corresponding timing.
     */
    public static TimingRepeatComponent getRepeatFromFrequencyCode(String frequencyCode) {
        TimingRepeatComponent repeat = new TimingRepeatComponent();
        if (frequencyCode != null && frequencyCode.equals("QD")) {
            repeat.setFrequency(1);
            repeat.setPeriod(24);
        }
        if (frequencyCode != null && frequencyCode.equals("Q8H")) {
            repeat.setFrequency(1);
            repeat.setPeriod(8);
        }
        return repeat;
    }
    
    /**
     * Parses a name using the active parser.
     *
     * @param name String form of name.
     * @return Parsed name.
     */
    public static HumanName parseName(String name) {
        return name == null ? null : defaultHumanNameParser.fromString(name);
    }
    
    /**
     * Processes a MethodOutcome from a create or update request. If the request returns an updated
     * version of the resource, that resource is returned. If the request returns a logical id, that
     * id is set in the original resource. If the request resulted in an error, a runtime exception
     * is thrown.
     *
     * @param <T> Resource type.
     * @param outcome The method outcome.
     * @param resource The resource upon which the method was performed.
     * @return If the method returned a new resource, that resource is returned. Otherwise, the
     *         original resource is returned, possibly with an updated logical id.
     */
    @SuppressWarnings("unchecked")
    public static <T extends IBaseResource> T processMethodOutcome(MethodOutcome outcome, T resource) {
        checkOutcome(outcome.getOperationOutcome());
        IIdType id = outcome.getId();
        IBaseResource newResource = outcome.getResource();
        
        if (id != null) {
            resource.setId(id);
        } else if (newResource != null && newResource.getClass() == resource.getClass()) {
            resource = (T) newResource;
        }
        
        return resource;
    }
    
    /**
     * Enforce static class.
     */
    private FhirUtil() {
    }
}