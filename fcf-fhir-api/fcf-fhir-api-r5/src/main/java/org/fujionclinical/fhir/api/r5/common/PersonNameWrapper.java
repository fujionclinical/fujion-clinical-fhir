package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r5.model.HumanName;
import org.hl7.fhir.r5.model.StringType;

import java.util.List;
import java.util.stream.Collectors;

public class PersonNameWrapper implements IPersonName, IWrapper<HumanName> {

    public static PersonNameWrapper wrap(HumanName name) {
        return name == null ? null : new PersonNameWrapper(name);
    }

    public static List<IPersonName> wrap(List<HumanName> names) {
        return names == null ? null : names.stream().map(name -> PersonNameWrapper.wrap(name)).collect(Collectors.toList());
    }

    public static List<HumanName> unwrap(List<IWrapper<HumanName>> names) {
        return names == null ? null : names.stream().map(name -> name.getWrapped()).collect(Collectors.toList());
    }

    private final HumanName name;

    private PersonNameWrapper(HumanName name) {
        this.name = name;
    }

    @Override
    public String getFamilyName() {
        return name.getFamily();
    }

    @Override
    public IPersonName setFamilyName(String familyName) {
        name.setFamily(familyName);
        return this;
    }

    @Override
    public List<String> getGivenNames() {
        return name.getGiven().stream().map(name -> name.toString()).collect(Collectors.toList());
    }

    @Override
    public IPersonName setGivenNames(List<String> givenNames) {
        name.setGiven(givenNames.stream().map(given -> new StringType(given)).collect(Collectors.toList()));
        return this;
    }

    @Override
    public List<String> getPrefixes() {
        return name.getPrefix().stream().map(prefix -> prefix.toString()).collect(Collectors.toList());
    }

    @Override
    public IPersonName setPrefixes(List<String> prefixes) {
        name.setPrefix(prefixes.stream().map(prefix -> new StringType(prefix)).collect(Collectors.toList()));
        return this;
    }

    @Override
    public List<String> getSuffixes() {
        return name.getSuffix().stream().map(suffix -> suffix.toString()).collect(Collectors.toList());
    }

    @Override
    public IPersonName setSuffixes(List<String> suffixes) {
        name.setSuffix(suffixes.stream().map(suffix -> new StringType(suffix)).collect(Collectors.toList()));
        return this;
    }

    @Override
    public PersonNameUse getUse() {
        return FhirUtilR5.convertEnum(name.getUse(), PersonNameUse.class);
    }

    @Override
    public IPersonName setUse(PersonNameUse category) {
        name.setUse(FhirUtilR5.convertEnum(category, HumanName.NameUse.class));
        return this;
    }

    @Override
    public HumanName getWrapped() {
        return name;
    }

    @Override
    public String toString() {
        return asString();
    }
}
