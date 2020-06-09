package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class AnnotationTransform implements IWrapperTransform<IAnnotation, AnnotationDt> {

    public static final AnnotationTransform instance = new AnnotationTransform();

    @Override
    public AnnotationDt _unwrap(IAnnotation value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAnnotation _wrap(AnnotationDt value) {
        return new AnnotationWrapper(value);
    }

}
