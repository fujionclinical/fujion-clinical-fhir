package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class ConceptTransform implements IWrapperTransform<IConcept, CodeableConceptDt> {

    public static final ConceptTransform instance = new ConceptTransform();

    @Override
    public IConcept _wrap(CodeableConceptDt value) {
        return new ConceptWrapper(value);
    }

    @Override
    public CodeableConceptDt newWrapped() {
        return new CodeableConceptDt();
    }

}
