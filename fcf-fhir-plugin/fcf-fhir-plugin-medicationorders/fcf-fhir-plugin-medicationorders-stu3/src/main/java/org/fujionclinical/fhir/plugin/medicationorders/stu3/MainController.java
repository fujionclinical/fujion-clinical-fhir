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
package org.fujionclinical.fhir.plugin.medicationorders.stu3;

import edu.utah.kmm.model.cool.mediator.fhir.stu3.common.FhirDataSource;
import org.fujion.common.StrUtil;
import org.fujionclinical.fhir.api.stu3.medication.MedicationService;
import org.fujionclinical.fhir.lib.sharedforms.BaseResourceListView;
import org.hl7.fhir.dstu3.model.Dosage;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.List;

/**
 * Controller for patient conditions display.
 */
public class MainController extends BaseResourceListView<MedicationRequest, MedicationRequest, FhirDataSource> {

    private final MedicationService service;

    public MainController(MedicationService service) {
        this.service = service;
    }

    @Override
    protected void setup() {
        setup(MedicationRequest.class, "Medication Orders", "Order Detail", "patient=#", 1, "Medication",
                "Date", "Status", "Sig");
    }

    @Override
    protected void populate(
            MedicationRequest script,
            List<Object> columns) {
        Object med = null;

        if (script.hasMedicationCodeableConcept()) {
            med = script.getMedication();
        } else if (script.hasMedicationReference()) {
            Medication medObject;
            medObject = getDataSource().getResource((Reference) script.getMedication(), Medication.class);
            med = medObject.getCode();
        }

        columns.add(med);
        columns.add(script.getAuthoredOn());
        columns.add(script.getStatus());
        columns.add(getSig(script.getDosageInstruction()));
    }

    private String getSig(List<Dosage> dosage) {
        StringBuilder sb = new StringBuilder();

        for (Dosage sig : dosage) {
            if (sb.length() > 0) {
                sb.append(StrUtil.CRLF);
            }

            if (sig.getText() != null) {
                sb.append(sig.getText());
            }
        }
        return sb.toString();
    }

    @Override
    protected void initModel(List<MedicationRequest> entries) {
        model.addAll(entries);
    }

}
