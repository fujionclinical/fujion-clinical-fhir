package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Annotation;

public class AnnotationTransform implements IWrapperTransform<IAnnotation, Annotation> {

    public static final AnnotationTransform instance = new AnnotationTransform();

    @Override
    public Annotation _unwrap(IAnnotation value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IAnnotation _wrap(Annotation value) {
        return new AnnotationWrapper(value);
    }

}
