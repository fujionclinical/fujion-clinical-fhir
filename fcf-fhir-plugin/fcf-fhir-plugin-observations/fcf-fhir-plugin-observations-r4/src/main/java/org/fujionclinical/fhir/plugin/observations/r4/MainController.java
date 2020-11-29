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
package org.fujionclinical.fhir.plugin.observations.r4;

import edu.utah.kmm.model.cool.mediator.fhir.r4.common.FhirDataSource;
import org.fujionclinical.fhir.lib.sharedforms.BaseResourceListView;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Type;

import java.util.List;

/**
 * Controller for patient observations display.
 */
public class MainController extends BaseResourceListView<Observation, MainController.ObservationResult, FhirDataSource> {

    public static class ObservationResult {

        private final CodeableConcept code;

        private final Type effective;

        private final Observation.ObservationStatus status;

        private final Type value;

        private final List<Observation.ObservationReferenceRangeComponent> referenceRange;

        public ObservationResult(Observation obs) {
            this.code = obs.getCode();
            this.effective = obs.getEffective();
            this.status = obs.getStatus();
            this.value = obs.getValue();
            this.referenceRange = obs.getReferenceRange();
        }

        public ObservationResult(
                Observation obs,
                Observation.ObservationComponentComponent cmp) {
            this.code = cmp.getCode();
            this.effective = obs.getEffective();
            this.status = obs.getStatus();
            this.value = cmp.getValue();
            this.referenceRange = cmp.getReferenceRange();
        }

    }

    @Override
    protected void setup() {
        setup(Observation.class, "Observations", "Observation Detail", "patient=#", 1, "Observation", "Date",
                "Status", "Result", "Ref Range");
    }

    @Override
    protected void populate(
            ObservationResult result,
            List<Object> columns) {
        columns.add(result.code);
        columns.add(result.effective);
        columns.add(result.status);
        columns.add(result.value);
        columns.add(renderReferenceRange(result.referenceRange));
    }

    @Override
    protected void initModel(List<Observation> entries) {
        for (Observation observation : entries) {
            if (observation.getComponent().isEmpty()) {
                model.add(new ObservationResult(observation));
            } else {
                for (Observation.ObservationComponentComponent cmp : observation.getComponent()) {
                    model.add(new ObservationResult(observation, cmp));
                }
            }
        }
    }

    private String renderReferenceRange(List<Observation.ObservationReferenceRangeComponent> ranges) {
        return null;
    }

}
