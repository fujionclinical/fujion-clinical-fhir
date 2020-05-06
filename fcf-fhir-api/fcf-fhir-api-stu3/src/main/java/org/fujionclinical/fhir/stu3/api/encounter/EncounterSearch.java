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
package org.fujionclinical.fhir.stu3.api.encounter;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import org.fujionclinical.fhir.stu3.api.query.BaseResourceQuery;
import org.hl7.fhir.dstu3.model.Encounter;

import java.util.Date;

/**
 * Encounter search implementation using FHIR.
 */
public class EncounterSearch extends BaseResourceQuery<Encounter, EncounterSearchCriteria> {

    public EncounterSearch(IGenericClient fhirClient) {
        super(Encounter.class, fhirClient);
    }

    @Override
    public void buildQuery(
            EncounterSearchCriteria criteria,
            IQuery<?> query) {
        super.buildQuery(criteria, query);

        if (criteria.getPatient() != null) {
            query.where(Encounter.PATIENT.hasId(criteria.getPatient().getId()));
        }

        if (criteria.getPeriod() != null) {
            Date start = criteria.getPeriod().getStart();
            Date end = criteria.getPeriod().getEnd();

            if (start != null) {
                if (end == null) {
                    query.where(Encounter.LOCATION_PERIOD.exactly().day(start));
                } else {
                    query.where(Encounter.LOCATION_PERIOD.afterOrEquals().day(start));
                    query.where(Encounter.LOCATION_PERIOD.beforeOrEquals().day(end));
                }
            } else if (end != null) {
                query.where(Encounter.LOCATION_PERIOD.exactly().day(end));
            }
        }

        if (criteria.getType() != null) {
            query.where(Encounter.TYPE.exactly().code(criteria.getType()));
        }
    }

}
