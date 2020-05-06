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
package org.fujionclinical.fhir.dstu2.api.practitioner;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import org.fujionclinical.fhir.dstu2.api.common.FhirUtil;
import org.fujionclinical.fhir.dstu2.api.query.BaseResourceQuery;

/**
 * Practitioner search implementation using FHIR.
 */
public class PractitionerSearch extends BaseResourceQuery<Practitioner, PractitionerSearchCriteria> {

    public PractitionerSearch(IGenericClient fhirClient) {
        super(Practitioner.class, fhirClient);
    }

    @Override
    public void buildQuery(
            PractitionerSearchCriteria criteria,
            IQuery<?> query) {
        super.buildQuery(criteria, query);
        IdentifierDt id = criteria.getDEA();

        if (id != null) {
            query.where(Practitioner.IDENTIFIER.exactly().systemAndIdentifier(id.getSystem(), id.getValue()));
        }

        id = criteria.getSSN();

        if (id != null) {
            query.where(Practitioner.IDENTIFIER.exactly().systemAndIdentifier(id.getSystem(), id.getValue()));
        }

        if (criteria.getGender() != null) {
            query.where(Practitioner.GENDER.exactly().code(criteria.getGender()));
        }

        if (criteria.getName() != null) {
            HumanNameDt name = criteria.getName();

            if (!name.getFamily().isEmpty()) {
                query.where(Practitioner.FAMILY.matches().values(FhirUtil.toStringList(name.getFamily())));
            }

            if (!name.getGiven().isEmpty()) {
                query.where(Practitioner.GIVEN.matches().values(FhirUtil.toStringList(name.getGiven())));
            }

        }
    }

}
