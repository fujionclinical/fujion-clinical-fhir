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
package org.fujionclinical.fhir.api.r5.observation;

import org.fujionclinical.api.model.core.IModelTransform;
import org.fujionclinical.api.model.core.ModelTransformRegistry;
import org.fujionclinical.api.model.observation.IObservationComponent;
import org.fujionclinical.api.model.observation.ObservationComponent;
import org.fujionclinical.fhir.api.common.transform.AbstractModelTransform;
import org.fujionclinical.fhir.api.r5.common.FhirUtilR5;
import org.fujionclinical.fhir.api.r5.transform.ConceptTransform;
import org.hl7.fhir.r5.model.DataType;
import org.hl7.fhir.r5.model.Observation.ObservationComponentComponent;

public class ObservationComponentTransform extends AbstractModelTransform<IObservationComponent, ObservationComponentComponent> {

    private static final ObservationComponentTransform instance = new ObservationComponentTransform();

    public static ObservationComponentTransform getInstance() {
        return instance;
    }

    private ObservationComponentTransform() {
        super(IObservationComponent.class, ObservationComponentComponent.class);
    }

    @Override
    public ObservationComponentComponent _fromLogicalModel(IObservationComponent src) {
        ObservationComponentComponent dest = new ObservationComponentComponent();
        dest.setCode(ConceptTransform.getInstance().fromLogicalModel(src.getCode()));
        dest.setReferenceRange(ReferenceRangeTransform.getInstance().fromLogicalModel(src.getReferenceRanges()));
        dest.setDataAbsentReason(FhirUtilR5.convertEnumToCodeableConcept(src.getDataAbsentReason(), "http://hl7.org/fhir/data-absent-reason"));
        Object value = src.getValue();

        if (value != null) {
            IModelTransform transform = ModelTransformRegistry.getInstance().get(value.getClass(), DataType.class);
            dest.setValue((DataType) transform.fromLogicalModel(value));
        }

        return dest;
    }

    @Override
    public IObservationComponent _toLogicalModel(ObservationComponentComponent src) {
        IObservationComponent dest = new ObservationComponent();
        dest.setCode(ConceptTransform.getInstance().toLogicalModel(src.getCode()));
        dest.setReferenceRanges(ReferenceRangeTransform.getInstance().toLogicalModel(src.getReferenceRange()));
        dest.setDataAbsentReason(FhirUtilR5.convertCodeableConceptToEnum(src.getDataAbsentReason(), IObservationComponent.DataAbsentReason.class));
        DataType value = src.getValue();

        if (value != null) {
            IModelTransform transform = ModelTransformRegistry.getInstance().get(Object.class, src.getValue().getClass());
            dest.setValue(transform.toLogicalModel(src.getValue()));
        }

        return dest;
    }

}
