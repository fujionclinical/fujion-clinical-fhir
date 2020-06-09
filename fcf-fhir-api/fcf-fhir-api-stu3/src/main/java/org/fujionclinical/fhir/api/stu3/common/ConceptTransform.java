package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.CodeableConcept;

public class ConceptTransform implements IWrapperTransform<IConcept, CodeableConcept> {

    public static final ConceptTransform instance = new ConceptTransform();

    @Override
    public CodeableConcept _unwrap(IConcept value) {
        return new CodeableConcept()
                .setText(value.getText())
                .setCoding(ConceptCodeTransform.instance.unwrap(value.getCodes()));
    }

    @Override
    public IConcept _wrap(CodeableConcept value) {
        return new ConceptWrapper(value);
    }

}
