package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.StringDt;
import org.apache.commons.lang3.EnumUtils;
import org.fujionclinical.api.model.ConceptCode;
import org.fujionclinical.api.model.Identifier;
import org.fujionclinical.api.model.PersonName;

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

    public static CodingDt conceptCode(ConceptCode conceptCode) {
        if (conceptCode == null) {
            return null;
        }

        CodingDt coding = new CodingDt(conceptCode.getSystem(), conceptCode.getCode());
        coding.setDisplay(conceptCode.getText());
        return coding;
    }

    public static ConceptCode conceptCode(CodingDt coding) {
        if (coding == null) {
            return null;
        }

        return new ConceptCode(coding.getSystem(), coding.getCode(), coding.getDisplay());
    }

    public static CodeableConceptDt conceptCodes(List<ConceptCode> conceptCodes) {
        if (conceptCodes == null) {
            return null;
        }

        CodeableConceptDt codeableConcept = new CodeableConceptDt();
        conceptCodes.forEach(conceptCode -> codeableConcept.addCoding(conceptCode(conceptCode)));
        return codeableConcept;
    }

    public static List<ConceptCode> conceptCodes(CodeableConceptDt codeableConcept) {
        if (codeableConcept == null) {
            return null;
        }

        List<ConceptCode> conceptCodes = new ArrayList<>();
        codeableConcept.getCoding().forEach(coding -> conceptCodes.add(conceptCode(coding)));
        return conceptCodes;
    }

    // --------------------- Identifier ---------------------

    public static Identifier identifier(IdentifierDt identifier) {
        return identifier == null ? null : new Identifier(identifier.getSystem(), identifier.getValue());
    }

    public static IdentifierDt identifier(Identifier identifier) {
        return identifier == null ? null : new IdentifierDt(identifier.getSystem(), identifier.getValue());
    }

    // --------------------- PersonName ---------------------

    public static PersonName personName(HumanNameDt humanName) {
        if (humanName == null) {
            return null;
        }

        PersonName personName = new PersonName();
        personName.setFamilyName(humanName.getFamilyAsSingleString());
        personName.setGivenNames(humanName.getGiven().stream().map(StringDt::toString).collect(Collectors.toList()));
        personName.setPrefixes(humanName.getPrefix().stream().map(StringDt::toString).collect(Collectors.toList()));
        personName.setSuffixes(humanName.getSuffix().stream().map(StringDt::toString).collect(Collectors.toList()));

        if (humanName.getUseElement() != null) {
            personName.setCategory(personNameCategory(humanName.getUseElement().getValueAsEnum()));
        }

        return personName;
    }

    public static HumanNameDt personName(PersonName personName) {
        if (personName == null) {
            return null;
        }

        HumanNameDt humanName = new HumanNameDt();

        if (personName.hasFamilyName()) {
            humanName.addFamily(personName.getFamilyName());
        }

        if (personName.hasGivenName()) {
            humanName.setGiven(personName.getGivenNames().stream().map(StringDt::new).collect(Collectors.toList()));
        }

        if (personName.hasPrefix()) {
            humanName.setPrefix(personName.getPrefixes().stream().map(StringDt::new).collect(Collectors.toList()));
        }

        if (personName.hasSuffix()) {
            humanName.setSuffix(personName.getSuffixes().stream().map(StringDt::new).collect(Collectors.toList()));
        }

        if (personName.hasCategory()) {
            humanName.setUse(personNameCategory(personName.getCategory()));
        }

        return humanName;
    }

    public static NameUseEnum personNameCategory(PersonName.PersonNameCategory category) {
        return EnumUtils.getEnum(NameUseEnum.class, category.name());
    }

    public static PersonName.PersonNameCategory personNameCategory(NameUseEnum nameUse) {
        return EnumUtils.getEnum(PersonName.PersonNameCategory.class, nameUse.name());
    }
}
