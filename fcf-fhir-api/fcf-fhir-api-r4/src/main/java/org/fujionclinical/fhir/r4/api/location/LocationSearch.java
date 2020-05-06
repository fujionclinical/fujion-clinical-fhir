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
package org.fujionclinical.fhir.r4.api.location;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import org.fujionclinical.fhir.r4.api.query.BaseResourceQuery;
import org.hl7.fhir.r4.model.Location;

/**
 * Location search implementation using FHIR.
 */
public class LocationSearch extends BaseResourceQuery<Location, LocationSearchCriteria> {

    public LocationSearch(IGenericClient fhirClient) {
        super(Location.class, fhirClient);
    }

    @Override
    public void buildQuery(
            LocationSearchCriteria criteria,
            IQuery<?> query) {
        super.buildQuery(criteria, query);

        if (criteria.getType() != null) {
            query.where(Location.TYPE.exactly().code(criteria.getType()));
        }

        if (criteria.getStatus() != null) {
            query.where(Location.STATUS.exactly().code(criteria.getStatus().toCode()));
        }

        if (criteria.getName() != null) {
            query.where(Location.NAME.matches().value(criteria.getName()));
        }
    }

}
