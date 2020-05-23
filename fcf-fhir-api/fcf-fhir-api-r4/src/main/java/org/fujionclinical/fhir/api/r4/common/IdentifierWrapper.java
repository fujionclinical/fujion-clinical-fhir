package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.IIdentifier;
import org.hl7.fhir.r4.model.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class IdentifierWrapper implements IIdentifier {

    public static IdentifierWrapper create(Identifier identifier) {
        return identifier == null ? null : new IdentifierWrapper(identifier);
    }

    public static List<IIdentifier> wrap(List<Identifier> identifiers) {
        return identifiers.stream().map(identifier -> IdentifierWrapper.create(identifier)).collect(Collectors.toList());
    }

    public static Identifier unwrap(IIdentifier identifier) {
        Identifier result = new Identifier()
                .setSystem(identifier.getSystem())
                .setValue(identifier.getValue());
        result.getType().setCoding(ConceptCodeWrapper.unwrap(identifier.getTypes()));
        return result;
    }

    private final Identifier identifier;

    private final List<IConceptCode> types;

    private IdentifierWrapper(Identifier identifer) {
        this.identifier = identifer;
        types = ConceptCodeWrapper.wrap(identifer.getType().getCoding());
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
    public List<IConceptCode> getTypes() {
        return types;
    }

    @Override
    public IdentifierCategory getCategory() {
        return FhirUtilR4.convertEnum(identifier.getUse(), IdentifierCategory.class);
    }

    @Override
    public IIdentifier setCategory(IdentifierCategory category) {
        identifier.setUse(FhirUtilR4.convertEnum(category, Identifier.IdentifierUse.class));
        return this;
    }
}
