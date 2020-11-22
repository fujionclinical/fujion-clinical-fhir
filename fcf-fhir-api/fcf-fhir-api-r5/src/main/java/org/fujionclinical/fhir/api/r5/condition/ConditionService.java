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
package org.fujionclinical.fhir.api.r5.condition;

import edu.utah.kmm.model.cool.mediator.fhir.core.AbstractFhirDataSource;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.Patient;

import java.util.Collections;
import java.util.List;

public class ConditionService {

    private final AbstractFhirDataSource dataSource;

    public ConditionService(AbstractFhirDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Condition> searchConditionsForPatient(Patient patient) {
        List<Condition> conditions = dataSource.searchResourcesForPatient(patient, Condition.class);
        Collections.sort(conditions, ConditionComparators.CONDITION_DATE_RECORDED);
        return conditions;
    }

}
