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
package org.fujionclinical.fhir.api.dstu2.transform;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import edu.utah.kmm.model.cool.foundation.datatype.Annotation;
import org.fujionclinical.api.model.impl.AnnotationImpl;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class AnnotationTransform extends AbstractDatatypeTransform<Annotation, AnnotationDt> {

    private static final AnnotationTransform instance = new AnnotationTransform();

    public static AnnotationTransform getInstance() {
        return instance;
    }

    private AnnotationTransform() {
        super(Annotation.class, AnnotationDt.class);
    }

    @Override
    public AnnotationDt _fromLogicalModel(Annotation src) {
        AnnotationDt dest = new AnnotationDt();
        dest.setAuthor(null); // TODO
        dest.setTime(DateTimeTransform.getInstance()._fromLogicalModel(src.getTimestamp()));
        dest.setText(src.getContent());
        return dest;
    }

    @Override
    public Annotation _toLogicalModel(AnnotationDt src) {
        Annotation dest = new AnnotationImpl();
        dest.setAuthor(null); // TODO
        dest.setTimestamp(DateTimeTransform.getInstance()._toLogicalModel(src.getTimeElement()));
        dest.setContent(src.getText());
        return dest;
    }

}
