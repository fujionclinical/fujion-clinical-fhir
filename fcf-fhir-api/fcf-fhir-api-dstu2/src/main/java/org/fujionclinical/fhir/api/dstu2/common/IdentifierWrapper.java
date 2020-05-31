package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IIdentifier;
import org.fujionclinical.api.model.IWrapper;

import java.util.List;
import java.util.stream.Collectors;

public class IdentifierWrapper implements IIdentifier, IWrapper<IdentifierDt> {

    private final IdentifierDt identifier;

    private final IConcept type;

    public static IdentifierWrapper wrap(IdentifierDt identifier) {
        return identifier == null ? null : new IdentifierWrapper(identifier);
    }

    public static List<IIdentifier> wrap(List<IdentifierDt> identifiers) {
        return identifiers == null ? null : identifiers.stream().map(identifier -> IdentifierWrapper.wrap(identifier)).collect(Collectors.toList());
    }

    public static IdentifierDt unwrap(IIdentifier identifier) {
        if (identifier == null) {
            return null;
        }

        IdentifierDt result = new IdentifierDt()
                .setSystem(identifier.getSystem())
                .setValue(identifier.getValue());
        result.getType().setText(identifier.getType().getText()).setCoding(ConceptCodeWrapper.unwrap(identifier.getType().getCodes()));
        return result;
    }

    private IdentifierWrapper(IdentifierDt identifer) {
        this.identifier = identifer;
        type = ConceptWrapper.wrap(identifer.getType());
    }

    @Override
    public String getSystem() {
        return identifier.getSystem();
    }

    @Override
    public void setSystem(String system) {
        identifier.setSystem(system);
    }

    @Override
    public String getValue() {
        return identifier.getValue();
    }

    @Override
    public void setValue(String value) {
        identifier.setValue(value);
    }

    @Override
    public IConcept getType() {
        return type;
    }

    @Override
    public IdentifierCategory getCategory() {
        return FhirUtilDstu2.convertEnum(identifier.getUseElement().getValueAsEnum(), IdentifierCategory.class);
    }

    @Override
    public void setCategory(IdentifierCategory category) {
        identifier.setUse(FhirUtilDstu2.convertEnum(category, IdentifierUseEnum.class));
    }

    @Override
    public IdentifierDt getWrapped() {
        return identifier;
    }

}
