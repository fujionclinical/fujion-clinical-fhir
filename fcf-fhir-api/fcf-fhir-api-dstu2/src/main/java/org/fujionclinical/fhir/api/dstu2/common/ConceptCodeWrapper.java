package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import org.fujionclinical.api.model.IConceptCode;
import org.fujionclinical.api.model.IWrapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConceptCodeWrapper implements IConceptCode, IWrapper<CodingDt> {

    private final CodingDt coding;

    public static ConceptCodeWrapper wrap(CodingDt coding) {
        return coding == null ? null : new ConceptCodeWrapper(coding);
    }

    public static List<IConceptCode> wrap(List<CodingDt> codings) {
        return codings == null ? Collections.emptyList() : codings.stream().map(coding -> ConceptCodeWrapper.wrap(coding)).collect(Collectors.toList());
    }

    public static List<IConceptCode> wrap(CodeableConceptDt codeableConcept) {
        return codeableConcept == null ? Collections.emptyList() : wrap(codeableConcept.getCoding());
    }

    public static CodingDt unwrap(IConceptCode code) {
        return code == null ? null : new CodingDt()
                .setSystem(code.getSystem())
                .setCode(code.getCode())
                .setDisplay(code.getText());
    }

    public static List<CodingDt> unwrap(List<IConceptCode> codes) {
        return codes == null ? Collections.emptyList() : codes.stream().map(code -> unwrap(code)).collect(Collectors.toList());
    }

    private ConceptCodeWrapper(CodingDt coding) {
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
    public CodingDt getWrapped() {
        return coding;
    }

}
