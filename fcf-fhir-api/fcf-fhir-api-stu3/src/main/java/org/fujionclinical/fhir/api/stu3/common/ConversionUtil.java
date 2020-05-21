package org.fujionclinical.fhir.api.stu3.common;

import org.apache.commons.lang3.EnumUtils;
import org.fujionclinical.api.model.ConceptCode;
import org.fujionclinical.api.model.PersonName;
import org.hl7.fhir.dstu3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Convert between FHIR model and logical model.  All conversions should be null safe.
 */
public class ConversionUtil {

    private ConversionUtil() {
    }

    // --------------------- ConceptCode ---------------------

    public static Coding conceptCode(ConceptCode conceptCode) {
        return conceptCode == null ? null : new Coding(conceptCode.getSystem(), conceptCode.getCode(), conceptCode.getText());
    }

    public static ConceptCode conceptCode(Coding coding) {
        if (coding == null) {
            return null;
        }

        return new ConceptCode(coding.getSystem(), coding.getCode(), coding.getDisplay());
    }

    public static CodeableConcept conceptCodes(List<ConceptCode> conceptCodes) {
        if (conceptCodes == null) {
            return null;
        }

        CodeableConcept codeableConcept = new CodeableConcept();
        conceptCodes.forEach(conceptCode -> codeableConcept.addCoding(conceptCode(conceptCode)));
        return codeableConcept;
    }

    public static List<ConceptCode> conceptCodes(CodeableConcept codeableConcept) {
        if (codeableConcept == null) {
            return null;
        }

        List<ConceptCode> conceptCodes = new ArrayList<>();
        codeableConcept.getCoding().forEach(coding -> conceptCodes.add(conceptCode(coding)));
        return conceptCodes;
    }

    // --------------------- Identifier ---------------------

    public static org.fujionclinical.api.model.Identifier identifier(Identifier identifier) {
        return identifier == null ? null : new org.fujionclinical.api.model.Identifier(identifier.getSystem(), identifier.getValue());
    }

    public static Identifier identifier(org.fujionclinical.api.model.Identifier identifier) {
        return identifier == null ? null : new Identifier().setSystem(identifier.getSystem()).setValue(identifier.getValue());
    }

    // --------------------- PersonName ---------------------

    public static PersonName personName(HumanName humanName) {
        if (humanName == null) {
            return null;
        }

        PersonName personName = new PersonName();
        personName.setFamilyName(humanName.getFamily());
        personName.setGivenNames(humanName.getGiven().stream().map(StringType::toString).collect(Collectors.toList()));
        personName.setPrefixes(humanName.getPrefix().stream().map(StringType::toString).collect(Collectors.toList()));
        personName.setSuffixes(humanName.getSuffix().stream().map(StringType::toString).collect(Collectors.toList()));

        if (humanName.getUseElement() != null) {
            personName.setCategory(personNameCategory(humanName.getUse()));
        }

        return personName;
    }

    public static HumanName personName(PersonName personName) {
        if (personName == null) {
            return null;
        }

        HumanName humanName = new HumanName();

        if (personName.hasFamilyName()) {
            humanName.setFamily(personName.getFamilyName());
        }

        if (personName.hasGivenName()) {
            humanName.setGiven(personName.getGivenNames().stream().map(StringType::new).collect(Collectors.toList()));
        }

        if (personName.hasPrefix()) {
            humanName.setPrefix(personName.getPrefixes().stream().map(StringType::new).collect(Collectors.toList()));
        }

        if (personName.hasSuffix()) {
            humanName.setSuffix(personName.getSuffixes().stream().map(StringType::new).collect(Collectors.toList()));
        }

        if (personName.hasCategory()) {
            humanName.setUse(personNameCategory(personName.getCategory()));
        }

        return humanName;
    }

    public static HumanName.NameUse personNameCategory(PersonName.PersonNameCategory category) {
        return EnumUtils.getEnum(HumanName.NameUse.class, category.name());
    }

    public static PersonName.PersonNameCategory personNameCategory(HumanName.NameUse nameUse) {
        return EnumUtils.getEnum(PersonName.PersonNameCategory.class, nameUse.name());
    }
}
