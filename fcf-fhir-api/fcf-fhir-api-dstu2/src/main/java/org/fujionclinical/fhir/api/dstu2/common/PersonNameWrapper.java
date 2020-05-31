package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.StringDt;
import org.fujionclinical.api.model.IWrapper;
import org.fujionclinical.api.model.person.IPersonName;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PersonNameWrapper implements IPersonName, IWrapper<HumanNameDt> {

    private final HumanNameDt name;

    public static PersonNameWrapper wrap(HumanNameDt name) {
        return name == null ? null : new PersonNameWrapper(name);
    }

    public static List<IPersonName> wrap(List<HumanNameDt> names) {
        return names == null ? null : names.stream().map(name -> PersonNameWrapper.wrap(name)).collect(Collectors.toList());
    }

    public static List<HumanNameDt> unwrap(List<IWrapper<HumanNameDt>> names) {
        return names == null ? null : names.stream().map(name -> name.getWrapped()).collect(Collectors.toList());
    }

    private PersonNameWrapper(HumanNameDt name) {
        this.name = name;
    }

    @Override
    public String getFamilyName() {
        return name.getFamilyAsSingleString();
    }

    @Override
    public void setFamilyName(String familyName) {
        name.setFamily(Collections.singletonList(new StringDt(familyName)));
    }

    @Override
    public List<String> getGivenNames() {
        return name.getGiven().stream().map(name -> name.toString()).collect(Collectors.toList());
    }

    @Override
    public void setGivenNames(List<String> givenNames) {
        name.setGiven(givenNames.stream().map(given -> new StringDt(given)).collect(Collectors.toList()));
    }

    @Override
    public List<String> getPrefixes() {
        return name.getPrefix().stream().map(prefix -> prefix.toString()).collect(Collectors.toList());
    }

    @Override
    public void setPrefixes(List<String> prefixes) {
        name.setPrefix(prefixes.stream().map(prefix -> new StringDt(prefix)).collect(Collectors.toList()));
    }

    @Override
    public List<String> getSuffixes() {
        return name.getSuffix().stream().map(suffix -> suffix.toString()).collect(Collectors.toList());
    }

    @Override
    public void setSuffixes(List<String> suffixes) {
        name.setSuffix(suffixes.stream().map(suffix -> new StringDt(suffix)).collect(Collectors.toList()));
    }

    @Override
    public PersonNameUse getUse() {
        return FhirUtilDstu2.convertEnum(name.getUse(), PersonNameUse.class);
    }

    @Override
    public void setUse(PersonNameUse category) {
        name.setUse(FhirUtilDstu2.convertEnum(category, NameUseEnum.class));
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
