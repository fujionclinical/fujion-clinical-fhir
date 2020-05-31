package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptCodeWrapper implements IConceptCode, IWrapper<Coding> {

    public static ConceptCodeWrapper wrap(Coding coding) {
        return coding == null ? null : new ConceptCodeWrapper(coding);
    }

    public static List<IConceptCode> wrap(CodeableConcept codeableConcept) {
        return codeableConcept == null ? Collections.emptyList() : wrap(codeableConcept.getCoding());
    }

    public static List<IConceptCode> wrap(List<Coding> codings) {
        return codings == null ? Collections.emptyList() : codings.stream().map(coding -> ConceptCodeWrapper.wrap(coding)).collect(Collectors.toList());
    }

    public static Coding unwrap(IConceptCode code) {
        return code == null ? null : new Coding()
                .setSystem(code.getSystem())
                .setCode(code.getCode())
                .setDisplay(code.getText());
    }

    public static List<Coding> unwrap(List<IConceptCode> codes) {
        return codes == null ? null : codes.stream().map(code -> unwrap(code)).collect(Collectors.toList());
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
    public void setSystem(String system) {
        coding.setSystem(system);
    }

    @Override
    public String getCode() {
        return coding.getCode();
    }

    @Override
    public void setCode(String code) {
        coding.setCode(code);
    }

    @Override
    public String getText() {
        return coding.getDisplay();
    }

    @Override
    public void setText(String text) {
        coding.setDisplay(text);
    }

    @Override
    public Coding getWrapped() {
        return coding;
    }
}
