package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Annotation;

public class AnnotationTransform implements IWrapperTransform<IAnnotation, Annotation> {

    public static final AnnotationTransform instance = new AnnotationTransform();

    @Override
    public IAnnotation _wrap(Annotation value) {
        return new AnnotationWrapper(value);
    }

    @Override
    public Annotation newWrapped() {
        return new Annotation();
    }

}
