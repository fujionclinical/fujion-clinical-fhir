package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r5.model.CodeableConcept;

public class ConceptTransform implements IWrapperTransform<IConcept, CodeableConcept> {

    public static final ConceptTransform instance = new ConceptTransform();

    @Override
    public IConcept _wrap(CodeableConcept value) {
        return new ConceptWrapper(value);
    }

    @Override
    public CodeableConcept newWrapped() {
        return new CodeableConcept();
    }

}
