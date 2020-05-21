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
package org.fujionclinical.fhir.plugin.adversereactions.dstu2;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance.Reaction;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.fujionclinical.fhir.lib.sharedforms.dstu2.controller.ResourceListView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for patient adverse reaction display.
 */
public class MainController extends ResourceListView<AllergyIntolerance, Reaction> {

    private static final Set<AllergyIntoleranceStatusEnum> exclusions = new HashSet<>();
    
    static {
        exclusions.add(AllergyIntoleranceStatusEnum.ENTERED_IN_ERROR);
        exclusions.add(AllergyIntoleranceStatusEnum.REFUTED);
        exclusions.add(AllergyIntoleranceStatusEnum.RESOLVED);
    }
    
    @Override
    protected void setup() {
        setup(AllergyIntolerance.class, Bundle.class, "Adverse Reactions", "Adverse Reaction Detail", "AllergyIntolerance?patient=#", 1,
            "Date^^min", "Agent", "Reaction");
    }
    
    @Override
    protected void render(Reaction adr, List<Object> columns) {
        columns.add(adr.getOnset());
        columns.add(adr.getSubstance().getCodingFirstRep().getDisplay());
        columns.add(getManifestations(adr.getManifestation()));
    }

    private String getManifestations(List<CodeableConceptDt> symptoms) {
        StringBuilder sb = new StringBuilder();

        for (CodeableConceptDt symptom : symptoms) {
            String sx = symptom.getText();

            if (StringUtils.isEmpty(sx)) {
                sx = symptom.getCodingFirstRep().getDisplay();
            }

            add(sx, sb);
        }

        return sb.toString();
    }


    private void add(String value, StringBuilder sb) {
        if (!StringUtils.isEmpty(value)) {
            sb.append(sb.length() == 0 ? "" : ", ").append(value);
        }
    }

    @Override
    protected void initModel(List<AllergyIntolerance> entries) {
        for (AllergyIntolerance adr : entries) {
            if (!exclusions.contains(adr.getStatusElement())) {
                model.addAll(adr.getReaction());
            }
        }
    }
    
}
