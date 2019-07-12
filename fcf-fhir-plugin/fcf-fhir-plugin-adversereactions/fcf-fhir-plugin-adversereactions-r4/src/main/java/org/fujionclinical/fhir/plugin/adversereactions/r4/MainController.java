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
package org.fujionclinical.fhir.plugin.adversereactions.r4;

import org.fujionclinical.fhir.lib.reports.r4.controller.ResourceListView;
import org.fujionclinical.fhir.r4.api.common.FhirUtil;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for patient adverse reaction display.
 */
public class MainController extends ResourceListView<AllergyIntolerance, AllergyIntolerance> {
    
    private static final Set<String> exclusions = new HashSet<>();
    
    static {
        exclusions.add("entered-in-error");
        exclusions.add("refuted");
    }
    
    @Override
    protected void setup() {
        setup(AllergyIntolerance.class, "Adverse Reactions", "Adverse Reaction Detail", "AllergyIntolerance?patient=#", 1,
            "Date^^min", "Agent", "Reaction");
    }
    
    @Override
    protected void render(AllergyIntolerance ai, List<Object> columns) {
        columns.add(ai.getRecordedDate());
        columns.add(ai.getCode());
        columns.add(getReactions(ai.getReaction()));
    }
    
    private String getReactions(List<AllergyIntoleranceReactionComponent> reactions) {
        StringBuilder sb = new StringBuilder();
        
        for (AllergyIntoleranceReactionComponent reaction : reactions) {
            String severity = reaction.hasSeverity() ? " (" + reaction.getSeverity().getDisplay() + ")" : "";
            String manifestation = reaction.hasManifestation() ? FhirUtil.getDisplayValue(reaction.getManifestation().get(0))
                    : "";
            sb.append(sb.length() == 0 ? "" : ", ");
            sb.append(manifestation).append(severity);
        }
        
        return sb.toString();
    }
    
    @Override
    protected void initModel(List<AllergyIntolerance> entries) {
        model.addAll(entries.stream().filter(entry ->
            entry.hasVerificationStatus() && !exclusions.contains(entry.getVerificationStatus().getCodingFirstRep().getCode())
        ).collect(Collectors.toList()));
    }
    
}
