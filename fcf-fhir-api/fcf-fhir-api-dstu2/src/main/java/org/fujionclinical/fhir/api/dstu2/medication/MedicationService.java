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
package org.fujionclinical.fhir.api.dstu2.medication;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import edu.utah.kmm.model.cool.mediator.fhir.dstu2.common.Dstu2Utils;
import edu.utah.kmm.model.cool.mediator.fhir.dstu2.common.FhirDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicationService {

    private final FhirDataSource dataSource;

    public MedicationService(FhirDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<MedicationAdministration> searchMedAdminByIdentifier(
            String system,
            String code) {
        IdentifierDt identifier = Dstu2Utils.createIdentifier(system, code);
        return searchMedAdminByIdentifier(identifier);
    }

    public List<MedicationAdministration> searchMedAdminByIdentifier(IdentifierDt identifier) {
        List<MedicationAdministration> meds = new ArrayList<>();

        Bundle patientBundle = dataSource.getClient()
                .search().forResource(MedicationAdministration.class).where(MedicationAdministration.IDENTIFIER.exactly()
                        .systemAndIdentifier(identifier.getSystem(), identifier.getValue()))
                .returnBundle(Bundle.class).execute();
        for (Bundle.Entry entry : patientBundle.getEntry()) {
            MedicationAdministration medication = (MedicationAdministration) entry.getResource();
            if (medication != null) {
                meds.add(medication);
            }
        }
        return meds;
    }

    public List<MedicationOrder> searchMedOrderByIdentifier(
            String system,
            String code) {
        IdentifierDt identifier = Dstu2Utils.createIdentifier(system, code);
        return dataSource.searchResourcesByIdentifier(identifier, MedicationOrder.class);
    }

    public List<MedicationAdministration> searchMedicationAdministrationsForPatient(Patient patient) {
        List<MedicationAdministration> results = dataSource.searchResourcesForPatient(patient, MedicationAdministration.class);
        Collections.sort(results, Comparators.MED_ADMIN_EFFECTIVE_TIME);
        return results;
    }

    public List<MedicationOrder> searchMedicationOrdersForPatient(Patient patient) {
        List<MedicationOrder> results = dataSource.searchResourcesForPatient(patient, MedicationOrder.class);
        Collections.sort(results, Comparators.MED_ORDER_DATE_WRITTEN);
        return results;
    }

}
