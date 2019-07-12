/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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

import org.fujionclinical.fhir.lib.reports.r4.controller.ResourceListView;
import org.fujionclinical.fhir.r4.api.common.FhirUtil;
import org.hl7.fhir.r4.model.Observation;

import java.util.List;

/**
 * Controller for patient observations display.
 */
public class MainController extends ResourceListView<Observation, Observation> {

    @Override
    protected void setup() {
        setup(Observation.class, "Observations", "Observation Detail", "Observation?patient=#", 1, "Observation", "Date",
            "Status", "Result", "Ref Range");
    }
    
    @Override
    protected void render(Observation observation, List<Object> columns) {
        columns.add(observation.getCode());
        columns.add(observation.getEffective());
        columns.add(observation.getStatus());
        columns.add(observation.getValue());
        
        if (observation.hasReferenceRange()) {
            columns.add(FhirUtil.getFirst(observation.getReferenceRange()).getText());
        } else {
            columns.add("");
        }
    }
    
    @Override
    protected void initModel(List<Observation> entries) {
        for (Observation observation : entries) {
            if (observation.getComponent().isEmpty()) {
                model.add(observation);
            }
        }
    }
    
}
