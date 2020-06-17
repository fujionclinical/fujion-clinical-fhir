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
package org.fujionclinical.fhir.plugin.diagnosticreports.dstu2;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.fujionclinical.fhir.lib.sharedforms.dstu2.controller.ResourceListView;

import java.util.List;

/**
 * Controller for patient diagnostic reports display.
 */
public class MainController extends ResourceListView<DiagnosticReport, DiagnosticReport> {

    @Override
    protected void setup() {
        setup(DiagnosticReport.class, Bundle.class, "Diagnostic Reports", "Report Detail", "DiagnosticReport?patient=#", 1, "Date", "Type",
                "Performed By", "Conclusion");
    }

    @Override
    protected void populate(
            DiagnosticReport report,
            List<Object> columns) {
        columns.add(report.getEffective());
        columns.add(report.getCode());
        columns.add(report.getPerformer().isEmpty() ? null : report.getPerformer().getDisplay().getValue());
        columns.add(report.getConclusion());
    }

    @Override
    protected void initModel(List<DiagnosticReport> entries) {
        model.addAll(entries);
    }

}
