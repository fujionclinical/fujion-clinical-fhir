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
package org.fujionclinical.fhir.api.dstu2.observation;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.fujionclinical.api.model.core.IModelTransform;
import org.fujionclinical.api.model.core.ModelTransforms;
import org.fujionclinical.api.model.observation.IObservationComponent;
import org.fujionclinical.api.model.observation.ObservationComponent;
import org.fujionclinical.fhir.api.common.transform.AbstractModelTransform;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtilDstu2;
import org.fujionclinical.fhir.api.dstu2.transform.ConceptTransform;

public class ObservationComponentTransform extends AbstractModelTransform<IObservationComponent, Observation.Component> {

    private static final ObservationComponentTransform instance = new ObservationComponentTransform();

    public static ObservationComponentTransform getInstance() {
        return instance;
    }

    private ObservationComponentTransform() {
        super(IObservationComponent.class, Observation.Component.class);
    }

    @Override
    public Observation.Component _fromLogicalModel(IObservationComponent src) {
        Observation.Component dest = new Observation.Component();
        dest.setCode(ConceptTransform.getInstance().fromLogicalModel(src.getCode()));
        dest.setReferenceRange(ReferenceRangeTransform.getInstance().fromLogicalModel(src.getReferenceRanges()));
        dest.setDataAbsentReason(FhirUtilDstu2.convertEnumToCodeableConcept(src.getDataAbsentReason(), "http://hl7.org/fhir/data-absent-reason"));
        Object value = src.getValue();

        if (value != null) {
            IModelTransform transform = ModelTransforms.getInstance().get(value.getClass(), IDatatype.class);
            dest.setValue((IDatatype) transform.fromLogicalModel(value));
        }

        return dest;
    }

    @Override
    public IObservationComponent _toLogicalModel(Observation.Component src) {
        IObservationComponent dest = new ObservationComponent();
        dest.setCode(ConceptTransform.getInstance().toLogicalModel(src.getCode()));
        dest.setReferenceRanges(ReferenceRangeTransform.getInstance().toLogicalModel(src.getReferenceRange()));
        dest.setDataAbsentReason(FhirUtilDstu2.convertCodeableConceptToEnum(src.getDataAbsentReason(), IObservationComponent.DataAbsentReason.class));
        IDatatype value = src.getValue();

        if (value != null) {
            IModelTransform transform = ModelTransforms.getInstance().get(Object.class, src.getValue().getClass());
            dest.setValue(transform.toLogicalModel(src.getValue()));
        }

        return dest;
    }

}
