package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.IWrapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptWrapper implements IConcept, IWrapper<CodeableConceptDt> {

    private final CodeableConceptDt codeableConcept;

    private final List<IConceptCode> codes;

    public static ConceptWrapper wrap(CodeableConceptDt codeableConcept) {
        return codeableConcept == null ? null : new ConceptWrapper(codeableConcept);
    }

    public static List<IConcept> wrap(List<CodeableConceptDt> concepts) {
        return concepts == null ? Collections.emptyList() : concepts.stream().map(concept -> ConceptWrapper.wrap(concept)).collect(Collectors.toList());
    }

    public static CodeableConceptDt unwrap(IConcept concept) {
        return concept == null ? null : new CodeableConceptDt()
                .setText(concept.getText())
                .setCoding(ConceptCodeWrapper.unwrap(concept.getCodes()));
    }

    private ConceptWrapper(CodeableConceptDt codeableConcept) {
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
    public CodeableConceptDt getWrapped() {
        return codeableConcept;
    }

}
