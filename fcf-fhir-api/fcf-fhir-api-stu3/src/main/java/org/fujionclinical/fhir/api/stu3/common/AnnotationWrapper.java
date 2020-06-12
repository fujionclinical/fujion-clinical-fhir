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
package org.fujionclinical.fhir.api.stu3.common;

import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.dstu3.model.Annotation;

import java.util.List;

public class AnnotationWrapper extends AbstractWrapper<Annotation> implements IAnnotation {

    protected AnnotationWrapper(Annotation annotation) {
        super(annotation);
    }

    @Override
    public List<IPerson> getAuthors() {
        return null;  // TODO
    }

    @Override
    public DateTimeWrapper getRecorded() {
        return FhirUtil.convertDate(getWrapped().getTime());
    }

    @Override
    public void setRecorded(DateTimeWrapper recorded) {
        getWrapped().setTime(FhirUtil.convertDate(recorded));
    }

    @Override
    public String getText() {
        return getWrapped().getText();
    }

    @Override
    public void setText(String text) {
        getWrapped().setText(text);
    }

}
