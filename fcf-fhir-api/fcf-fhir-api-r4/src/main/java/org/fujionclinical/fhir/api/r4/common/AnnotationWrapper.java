/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
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
