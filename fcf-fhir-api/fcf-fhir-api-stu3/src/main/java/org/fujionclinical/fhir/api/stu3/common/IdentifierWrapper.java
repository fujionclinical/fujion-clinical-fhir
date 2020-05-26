package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IIdentifier;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.dstu3.model.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class IdentifierWrapper implements IIdentifier, IWrapper<Identifier> {

    private final Identifier identifier;

    private final IConcept type;

    public static IdentifierWrapper wrap(Identifier identifier) {
        return identifier == null ? null : new IdentifierWrapper(identifier);
    }

    public static List<IIdentifier> wrap(List<Identifier> identifiers) {
        return identifiers == null ? null : identifiers.stream().map(identifier -> IdentifierWrapper.wrap(identifier)).collect(Collectors.toList());
    }

    public static Identifier unwrap(IIdentifier identifier) {
        if (identifier == null) {
            return null;
        }

        Identifier result = new Identifier()
                .setSystem(identifier.getSystem())
                .setValue(identifier.getValue());
        result.setType(ConceptWrapper.unwrap(identifier.getType()));
        return result;
    }

    private IdentifierWrapper(Identifier identifer) {
        this.identifier = identifer;
        type = ConceptWrapper.wrap(identifer.getType());
    }

    @Override
    public String getSystem() {
        return identifier.getSystem();
    }

    @Override
    public IIdentifier setSystem(String system) {
        identifier.setSystem(system);
        return this;
    }

    @Override
    public String getValue() {
        return identifier.getValue();
    }

    @Override
    public IIdentifier setValue(String value) {
        identifier.setValue(value);
        return this;
    }

    @Override
    public IConcept getType() {
        return type;
    }

    @Override
    public IdentifierCategory getCategory() {
        return FhirUtilStu3.convertEnum(identifier.getUse(), IdentifierCategory.class);
    }

    @Override
    public IIdentifier setCategory(IdentifierCategory category) {
        identifier.setUse(FhirUtilStu3.convertEnum(category, Identifier.IdentifierUse.class));
        return this;
    }

    @Override
    public Identifier getWrapped() {
        return identifier;
    }

}
