package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.StringDt;
import org.apache.commons.lang3.EnumUtils;
import org.fujion.common.DateRange;
import org.fujionclinical.api.model.Address;
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

    // --------------------- DateRange ---------------------

    public static PeriodDt dateRange(DateRange dateRange) {
        if (dateRange == null) {
            return null;
        }

        PeriodDt period = new PeriodDt();
        period.setStartWithSecondsPrecision(dateRange.getStartDate());
        period.setEndWithSecondsPrecision(dateRange.getEndDate());
        return period;
    }

    public static DateRange dateRange(PeriodDt period) {
        return period == null ? null : new DateRange(period.getStart(), period.getEnd());
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

        if (!humanName.getUseElement().isEmpty()) {
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
        return category == null ? null : EnumUtils.getEnum(NameUseEnum.class, category.name());
    }

    public static PersonName.PersonNameCategory personNameCategory(NameUseEnum nameUse) {
        return nameUse == null ? null : EnumUtils.getEnum(PersonName.PersonNameCategory.class, nameUse.name());
    }

    // --------------------- Address ---------------------

    public static AddressDt address(Address address) {
        if (address == null) {
            return null;
        }

        AddressDt addr = new AddressDt();
        addr.setCity(address.getCity());
        addr.setCountry(address.getCountry());
        addr.setDistrict(address.getDistrict());
        addr.setPostalCode(address.getPostalCode());
        addr.setState(address.getState());
        addr.setUse(addressCategory(address.getCategory()));
        addr.setPeriod(dateRange(address.getPeriod()));
        return addr;
    }

    public static Address address(AddressDt address) {
        if (address == null) {
            return null;
        }

        Address addr = new Address();
        addr.setCity(address.getCity());
        addr.setCountry(address.getCountry());
        addr.setDistrict(address.getDistrict());
        addr.setPostalCode(address.getPostalCode());
        addr.setState(address.getState());
        addr.setCategory(addressCategory(address.getUseElement().isEmpty() ? null : address.getUseElement().getValueAsEnum()));
        addr.setPeriod(dateRange(address.getPeriod()));
        return addr;
    }

    public static AddressUseEnum addressCategory(Address.AddressCategory category) {
        return category == null ? null : EnumUtils.getEnum(AddressUseEnum.class, category.name());
    }

    public static Address.AddressCategory addressCategory(AddressUseEnum addressUse) {
        return addressUse == null ? null : EnumUtils.getEnum(Address.AddressCategory.class, addressUse.name());
    }

}
