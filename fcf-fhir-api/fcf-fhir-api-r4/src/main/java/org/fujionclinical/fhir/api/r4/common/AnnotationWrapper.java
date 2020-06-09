package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IWrapper;
import org.fujionclinical.api.model.person.IPerson;
import org.hl7.fhir.r4.model.Annotation;

import java.util.Date;
import java.util.List;

public class AnnotationWrapper implements IAnnotation, IWrapper<Annotation> {

    private final Annotation annotation;

    protected AnnotationWrapper(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public List<IPerson> getAuthors() {
        return null;  // TODO
    }

    @Override
    public Date getRecorded() {
        return annotation.getTime();
    }

    @Override
    public void setRecorded(Date recorded) {
        annotation.setTime(recorded);
    }

    @Override
    public String getText() {
        return annotation.getText();
    }

    @Override
    public void setText(String text) {
        annotation.setText(text);
    }

    @Override
    public Annotation getWrapped() {
        return annotation;
    }

}
