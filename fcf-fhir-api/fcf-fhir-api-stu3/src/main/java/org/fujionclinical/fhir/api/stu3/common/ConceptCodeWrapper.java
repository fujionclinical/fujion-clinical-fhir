package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.IConceptCode;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptCodeWrapper implements IConceptCode {

    public static ConceptCodeWrapper create(Coding coding) {
        return coding == null ? null : new ConceptCodeWrapper(coding);
    }

    public static List<IConceptCode> wrap(CodeableConcept codeableConcept) {
        return codeableConcept == null ? Collections.emptyList() : wrap(codeableConcept.getCoding());
    }

    public static List<IConceptCode> wrap(List<Coding> codings) {
        return codings == null ? Collections.emptyList() : codings.stream().map(coding -> ConceptCodeWrapper.create(coding)).collect(Collectors.toList());
    }

    public static Coding unwrap(IConceptCode code) {
        return code == null ? null : new Coding()
                .setSystem(code.getSystem())
                .setCode(code.getCode())
                .setDisplay(code.getText());
    }

    public static List<Coding> unwrap(List<IConceptCode> codes) {
        return codes.stream().map(code -> unwrap(code)).collect(Collectors.toList());
    }

    private final Coding coding;

    private ConceptCodeWrapper(Coding coding) {
        this.coding = coding;
    }

    @Override
    public String getSystem() {
        return coding.getSystem();
    }

    @Override
    public IConceptCode setSystem(String system) {
        coding.setSystem(system);
        return this;
    }

    @Override
    public String getCode() {
        return coding.getCode();
    }

    @Override
    public IConceptCode setCode(String code) {
        coding.setCode(code);
        return this;
    }

    @Override
    public String getText() {
        return coding.getDisplay();
    }

    @Override
    public IConceptCode setText(String text) {
        coding.setDisplay(text);
        return this;
    }
}
