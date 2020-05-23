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
package org.fujionclinical.fhir.api.stu3.patient;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.api.patient.search.IPatientSearchEngine;
import org.fujionclinical.api.patient.search.PatientSearchCriteria;
import org.fujionclinical.fhir.api.stu3.common.FhirUtilStu3;
import org.fujionclinical.fhir.api.stu3.query.BaseResourceQuery;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.List;
import java.util.stream.Collectors;

import static org.fujionclinical.fhir.api.common.core.Constants.SSN_SYSTEM;

/**
 * Patient search implementation using FHIR.
 */
public class PatientSearch extends BaseResourceQuery<Patient, PatientSearchCriteria> implements IPatientSearchEngine {

    public PatientSearch(IGenericClient fhirClient) {
        super(Patient.class, fhirClient);
    }

    @Override
    public void buildQuery(
            PatientSearchCriteria criteria,
            IQuery<?> query) {
        super.buildQuery(criteria, query);
        String id = criteria.getMRN();

        if (id != null) {
            query.where(Patient.IDENTIFIER.exactly().identifier(id));
        }

        id = criteria.getSSN();

        if (id != null) {
            query.where(Patient.IDENTIFIER.exactly().systemAndIdentifier(SSN_SYSTEM, id));
        }

        if (criteria.getBirth() != null) {
            query.where(Patient.BIRTHDATE.exactly().day(criteria.getBirth()));
        }

        if (criteria.getGender() != null) {
            query.where(Patient.GENDER.exactly().code(criteria.getGender()));
        }

        if (criteria.getName() != null) {
            IPersonName name = criteria.getName();

            if (name.hasFamilyName()) {
                query.where(Patient.FAMILY.matches().value(name.getFamilyName()));
            }

            if (name.hasGivenName()) {
                query.where(Patient.GIVEN.matches().values(FhirUtilStu3.toStringList(name.getGivenNames())));
            }

        }
    }

    @Override
    public List<IPatient> search(PatientSearchCriteria criteria) {
        return query(criteria).stream().map(patient -> PatientWrapper.create(patient)).collect(Collectors.toList());
    }

}
