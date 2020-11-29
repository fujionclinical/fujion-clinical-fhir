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
package org.fujionclinical.fhir.plugin.procedures.r4;

import edu.utah.kmm.model.cool.mediator.fhir.r4.common.FhirDataSource;
import org.fujionclinical.fhir.lib.sharedforms.BaseResourceListView;
import org.hl7.fhir.r4.model.Procedure;

import java.util.List;

/**
 * Controller for patient procedures display.
 */
public class MainController extends BaseResourceListView<Procedure, Procedure, FhirDataSource> {

    @Override
    protected void setup() {
        setup(Procedure.class, "Procedures", "Procedure Detail", "patient=#", 1, "Procedure", "Date", "Status",
                "Notes");
    }

    @Override
    protected void populate(
            Procedure procedure,
            List<Object> columns) {
        columns.add(procedure.getCode());
        columns.add(procedure.getPerformed());
        columns.add(procedure.getStatus());
        columns.add(procedure.getNote());
    }

    @Override
    protected void initModel(List<Procedure> entries) {
        model.addAll(entries);
    }

}
