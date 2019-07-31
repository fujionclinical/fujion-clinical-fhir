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
package org.fujionclinical.fhir.plugin.encounters.r4;

import org.fujionclinical.fhir.lib.reports.r4.controller.ResourceListView;
import org.hl7.fhir.r4.model.Encounter;

import java.util.List;

/**
 * Controller for patient encounters display.
 */
public class MainController extends ResourceListView<Encounter, Encounter> {

    @Override
    protected void setup() {
        setup(Encounter.class, "Encounters", "Encounter Detail", "Encounter?patient=#", 1, "Date", "Status", "Location", "Providers");
    }

    @Override
    protected void render(Encounter encounter, List<Object> columns) {
        columns.add(encounter.getPeriod());
        columns.add(encounter.getStatus());
        columns.add(encounter.getLocation());
        columns.add(encounter.getParticipant());
    }

    @Override
    protected void initModel(List<Encounter> entries) {
        model.addAll(entries);
    }

}
