package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.StringDt;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.IWrapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PersonNameWrapper implements IPersonName, IWrapper<HumanNameDt> {

    public static PersonNameWrapper create(HumanNameDt name) {
        return name == null ? null : new PersonNameWrapper(name);
    }

    public static List<IPersonName> wrap(List<HumanNameDt> names) {
        return names.stream().map(name -> PersonNameWrapper.create(name)).collect(Collectors.toList());
    }

    public static List<HumanNameDt> unwrap(List<IWrapper<HumanNameDt>> names) {
        return names.stream().map(name -> name.getWrapped()).collect(Collectors.toList());
    }

    private final HumanNameDt name;

    private PersonNameWrapper(HumanNameDt name) {
        this.name = name;
    }

    @Override
    public String getFamilyName() {
        return name.getFamilyAsSingleString();
    }

    @Override
    public IPersonName setFamilyName(String familyName) {
        name.setFamily(Collections.singletonList(new StringDt(familyName)));
        return this;
    }

    @Override
    public List<String> getGivenNames() {
        return name.getGiven().stream().map(name -> name.toString()).collect(Collectors.toList());
    }

    @Override
    public IPersonName setGivenNames(List<String> givenNames) {
        name.setGiven(givenNames.stream().map(given -> new StringDt(given)).collect(Collectors.toList()));
        return this;
    }

    @Override
    public List<String> getPrefixes() {
        return name.getPrefix().stream().map(prefix -> prefix.toString()).collect(Collectors.toList());
    }

    @Override
    public IPersonName setPrefixes(List<String> prefixes) {
        name.setPrefix(prefixes.stream().map(prefix -> new StringDt(prefix)).collect(Collectors.toList()));
        return this;
    }

    @Override
    public List<String> getSuffixes() {
        return name.getSuffix().stream().map(suffix -> suffix.toString()).collect(Collectors.toList());
    }

    @Override
    public IPersonName setSuffixes(List<String> suffixes) {
        name.setSuffix(suffixes.stream().map(suffix -> new StringDt(suffix)).collect(Collectors.toList()));
        return this;
    }

    @Override
    public PersonNameUse getUse() {
        return FhirUtilDstu2.convertEnum(name.getUse(), PersonNameUse.class);
    }

    @Override
    public IPersonName setUse(PersonNameUse category) {
        name.setUse(FhirUtilDstu2.convertEnum(category, NameUseEnum.class));
        return this;
    }

    @Override
    public HumanNameDt getWrapped() {
        return name;
    }

    @Override
    public String toString() {
        return asString();
    }
}
