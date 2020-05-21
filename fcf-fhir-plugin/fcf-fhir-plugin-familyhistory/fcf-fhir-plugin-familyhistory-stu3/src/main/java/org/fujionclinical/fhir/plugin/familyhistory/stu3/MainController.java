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
package org.fujionclinical.fhir.plugin.familyhistory.stu3;

import org.fujion.component.Div;
import org.fujion.component.Label;
import org.fujionclinical.fhir.api.stu3.common.FhirUtil;
import org.fujionclinical.fhir.lib.sharedforms.stu3.controller.ResourceListView;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.FamilyMemberHistory;
import org.hl7.fhir.dstu3.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent;

import java.util.List;

/**
 * Controller for family history display.
 */
public class MainController extends ResourceListView<FamilyMemberHistory, FamilyMemberHistory> {

    @Override
    protected void setup() {
        setup(FamilyMemberHistory.class, Bundle.class, "Family History", "Family History Detail", "FamilyMemberHistory?patient=#", 1,
            "Relation", "Condition", "Outcome", "Notes");
    }

    @Override
    protected void render(FamilyMemberHistory relation, List<Object> columns) {
        columns.add(relation.getRelationship());

        for (int i = 0; i < 3; i++) {
            Div cmp = new Div();
            cmp.addClass("fujion-layout-vertical");
            columns.add(cmp);

            for (FamilyMemberHistoryConditionComponent condition : relation.getCondition()) {
                String value = null;

                switch (i) {
                    case 0:
                        value = FhirUtil.getDisplayValue(condition.getCode());
                        break;

                    case 1:
                        value = FhirUtil.getDisplayValue(condition.getOutcome());
                        break;

                    case 2:
                        value = FhirUtil.getDisplayValueForTypes(condition.getNote(), "\n\n");
                        break;
                }

                cmp.addChild(new Label(value));
            }

        }
    }

    @Override
    protected void initModel(List<FamilyMemberHistory> entries) {
        model.addAll(entries);
    }

}
