package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r4.model.CodeableConcept;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptWrapper implements IConcept, IWrapper<CodeableConcept> {

    private final CodeableConcept codeableConcept;

    private final List<IConceptCode> codes;

    public static ConceptWrapper wrap(CodeableConcept codeableConcept) {
        return codeableConcept == null ? null : new ConceptWrapper(codeableConcept);
    }

    public static List<IConcept> wrap(List<CodeableConcept> concepts) {
        return concepts == null ? Collections.emptyList() : concepts.stream().map(concept -> ConceptWrapper.wrap(concept)).collect(Collectors.toList());    
    }
    
    public static CodeableConcept unwrap(IConcept concept) {
        return concept == null ? null : new CodeableConcept()
                .setText(concept.getText())
                .setCoding(ConceptCodeWrapper.unwrap(concept.getCodes()));
    }

    private ConceptWrapper(CodeableConcept codeableConcept) {
        this.codeableConcept = codeableConcept;
        this.codes = ConceptCodeWrapper.wrap(codeableConcept.getCoding());
    }

    @Override
    public String getText() {
        return codeableConcept.getText();
    }

    @Override
    public IConcept setText(String text) {
        codeableConcept.setText(text);
        return this;
    }

    @Override
    public List<IConceptCode> getCodes() {
        return codes;
    }

    @Override
    public CodeableConcept getWrapped() {
        return codeableConcept;
    }

}
