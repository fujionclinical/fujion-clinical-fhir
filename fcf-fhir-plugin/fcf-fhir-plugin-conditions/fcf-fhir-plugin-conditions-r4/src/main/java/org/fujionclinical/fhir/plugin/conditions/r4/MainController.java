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
package org.fujionclinical.fhir.plugin.conditions.r4;

import org.fujionclinical.fhir.lib.sharedforms.r4.controller.ResourceListView;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;

import java.util.List;

/**
 * Controller for patient conditions display.
 */
public class MainController extends ResourceListView<Condition, Condition> {
    
    @Override
    protected void setup() {
        setup(Condition.class, Bundle.class, "Conditions", "Condition Detail", "Condition?patient=#", 1, "Condition", "Onset", "Status",
                "Notes");
    }
    
    @Override
    protected void render(Condition condition, List<Object> columns) {
        columns.add(condition.getCode());
        columns.add(condition.getOnset());
        columns.add(condition.getClinicalStatus());
        columns.add(condition.getNote());
    }
    
    @Override
    protected void initModel(List<Condition> entries) {
        model.addAll(entries);
    }
    
}
