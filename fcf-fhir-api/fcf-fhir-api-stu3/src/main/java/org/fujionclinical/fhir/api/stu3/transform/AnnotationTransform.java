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
package org.fujionclinical.fhir.api.stu3.transform;

import edu.utah.kmm.model.cool.foundation.datatype.AnnotationImpl;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.dstu3.model.Annotation;

public class AnnotationTransform extends AbstractDatatypeTransform<edu.utah.kmm.model.cool.foundation.datatype.Annotation, Annotation> {

    private static final AnnotationTransform instance = new AnnotationTransform();

    public static AnnotationTransform getInstance() {
        return instance;
    }

    private AnnotationTransform() {
        super(edu.utah.kmm.model.cool.foundation.datatype.Annotation.class, Annotation.class);
    }

    @Override
    public Annotation _fromLogicalModel(edu.utah.kmm.model.cool.foundation.datatype.Annotation src) {
        Annotation dest = new Annotation();
        dest.setAuthor(null); // TODO
        dest.setTimeElement(DateTimeTransform.getInstance()._fromLogicalModel(src.getTimestamp()));
        dest.setText(src.getContent());
        return dest;
    }

    @Override
    public edu.utah.kmm.model.cool.foundation.datatype.Annotation _toLogicalModel(Annotation src) {
        edu.utah.kmm.model.cool.foundation.datatype.Annotation dest = new AnnotationImpl(null, src.getText()); // TODO: author
        dest.setTimestamp(DateTimeTransform.getInstance()._toLogicalModel(src.getTimeElement()));
        return dest;
    }

}
