package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.CodeableConcept;

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
