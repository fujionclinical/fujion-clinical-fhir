package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.IIdentifier;

import java.util.List;
import java.util.stream.Collectors;

public class IdentifierWrapper implements IIdentifier {

    public static IdentifierWrapper create(IdentifierDt identifier) {
        return identifier == null ? null : new IdentifierWrapper(identifier);
    }

    public static List<IIdentifier> wrap(List<IdentifierDt> identifiers) {
        return identifiers.stream().map(identifier -> IdentifierWrapper.create(identifier)).collect(Collectors.toList());
    }

    public static IdentifierDt unwrap(IIdentifier identifier) {
        IdentifierDt result = new IdentifierDt()
                .setSystem(identifier.getSystem())
                .setValue(identifier.getValue());
        result.getType().setCoding(ConceptCodeWrapper.unwrap(identifier.getTypes()));
        return result;
    }

    private final IdentifierDt identifier;

    private final List<IConceptCode> types;

    private IdentifierWrapper(IdentifierDt identifer) {
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
        return FhirUtil.convertEnum(identifier.getUseElement().getValueAsEnum(), IdentifierCategory.class);
    }

    @Override
    public IIdentifier setCategory(IdentifierCategory category) {
        identifier.setUse(FhirUtil.convertEnum(category, IdentifierUseEnum.class));
        return this;
    }
}
