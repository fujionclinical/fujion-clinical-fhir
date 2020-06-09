package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class ConceptCodeTransform implements IWrapperTransform<IConceptCode, CodingDt> {

    public static final ConceptCodeTransform instance = new ConceptCodeTransform();

    @Override
    public CodingDt _unwrap(IConceptCode value) {
        return new CodingDt()
                .setSystem(value.getSystem())
                .setCode(value.getCode())
                .setDisplay(value.getText());
    }

    @Override
    public IConceptCode _wrap(CodingDt value) {
        return new ConceptCodeWrapper(value);
    }

}
