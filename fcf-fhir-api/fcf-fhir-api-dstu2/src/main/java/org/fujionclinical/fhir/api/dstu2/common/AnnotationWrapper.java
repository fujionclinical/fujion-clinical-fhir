package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IWrapper;
import org.fujionclinical.api.model.person.IPerson;

import java.util.Date;
import java.util.List;

public class AnnotationWrapper implements IAnnotation, IWrapper<AnnotationDt> {

    private final AnnotationDt annotation;

    protected AnnotationWrapper(AnnotationDt annotation) {
        this.annotation = annotation;
    }

    @Override
    public List<IPerson> getAuthors() {
        return null; // TODO
    }

    @Override
    public Date getRecorded() {
        return annotation.getTime();
    }

    @Override
    public void setRecorded(Date recorded) {
        annotation.setTime(recorded, TemporalPrecisionEnum.SECOND);
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
    public AnnotationDt getWrapped() {
        return annotation;
    }

}
