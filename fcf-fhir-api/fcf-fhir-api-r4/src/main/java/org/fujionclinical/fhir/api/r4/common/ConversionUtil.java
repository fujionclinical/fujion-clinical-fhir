package org.fujionclinical.fhir.api.r4.common;

import org.apache.commons.lang3.EnumUtils;
import org.fujion.common.DateRange;
import org.fujionclinical.api.model.ConceptCode;
import org.fujionclinical.api.model.PersonName;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Convert between FHIR model and logical model.  All conversions should be null safe.
 */
public class ConversionUtil {

    private ConversionUtil() {
    }

    // --------------------- DateRange ---------------------

    public static Period dateRange(DateRange dateRange) {
        if (dateRange == null) {
            return null;
        }

        Period period = new Period();
        period.setStart(dateRange.getStartDate());
        period.setEnd(dateRange.getEndDate());
        return period;
    }

    public static DateRange dateRange(Period period) {
        return period == null ? null : new DateRange(period.getStart(), period.getEnd());
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

        if (humanName.hasUse()) {
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
        return category == null ? null : EnumUtils.getEnum(HumanName.NameUse.class, category.name());
    }

    public static PersonName.PersonNameCategory personNameCategory(HumanName.NameUse nameUse) {
        return nameUse == null ? null : EnumUtils.getEnum(PersonName.PersonNameCategory.class, nameUse.name());
    }

    // --------------------- Address ---------------------

    public static Address address(org.fujionclinical.api.model.Address address) {
        if (address == null) {
            return null;
        }

        Address addr = new Address();
        addr.setCity(address.getCity());
        addr.setCountry(address.getCountry());
        addr.setDistrict(address.getDistrict());
        addr.setPostalCode(address.getPostalCode());
        addr.setState(address.getState());
        addr.setUse(addressCategory(address.getCategory()));
        addr.setPeriod(dateRange(address.getPeriod()));
        return addr;
    }

    public static org.fujionclinical.api.model.Address address(Address address) {
        if (address == null) {
            return null;
        }

        org.fujionclinical.api.model.Address addr = new org.fujionclinical.api.model.Address();
        addr.setCity(address.getCity());
        addr.setCountry(address.getCountry());
        addr.setDistrict(address.getDistrict());
        addr.setPostalCode(address.getPostalCode());
        addr.setState(address.getState());
        addr.setCategory(addressCategory(address.getUseElement().isEmpty() ? null : address.getUse()));
        addr.setPeriod(dateRange(address.getPeriod()));
        return addr;
    }

    public static Address.AddressUse addressCategory(org.fujionclinical.api.model.Address.AddressCategory category) {
        return category == null ? null : EnumUtils.getEnum(Address.AddressUse.class, category.name());
    }

    public static org.fujionclinical.api.model.Address.AddressCategory addressCategory(Address.AddressUse addressUse) {
        return addressUse == null ? null : EnumUtils.getEnum(org.fujionclinical.api.model.Address.AddressCategory.class, addressUse.name());
    }

}
