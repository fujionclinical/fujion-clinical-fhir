package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r5.model.Coding;

public class ConceptCodeTransform implements IWrapperTransform<IConceptCode, Coding> {

    public static final ConceptCodeTransform instance = new ConceptCodeTransform();

    @Override
    public IConceptCode _wrap(Coding value) {
        return new ConceptCodeWrapper(value);
    }

    @Override
    public Coding newWrapped() {
        return new Coding();
    }

}
