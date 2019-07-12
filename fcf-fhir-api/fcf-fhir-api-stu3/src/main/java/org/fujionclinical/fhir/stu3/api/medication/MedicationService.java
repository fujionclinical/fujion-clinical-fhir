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
package org.fujionclinical.fhir.stu3.api.medication;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.fhir.stu3.api.common.BaseService;
import org.fujionclinical.fhir.stu3.api.common.FhirUtil;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicationService extends BaseService {

    public MedicationService(IGenericClient client) {
        super(client);
    }

    public List<MedicationAdministration> searchMedAdminByIdentifier(String system, String code) {
        Identifier identifier = FhirUtil.createIdentifier(system, code);
        return searchMedAdminByIdentifier(identifier);
    }

    public List<MedicationAdministration> searchMedAdminByIdentifier(Identifier identifier) {
        List<MedicationAdministration> meds = new ArrayList<>();

        Bundle patientBundle = getClient()
                .search().forResource(MedicationAdministration.class).where(MedicationAdministration.IDENTIFIER.exactly()
                        .systemAndIdentifier(identifier.getSystem(), identifier.getValue()))
                .returnBundle(Bundle.class).execute();
        for (BundleEntryComponent entry : patientBundle.getEntry()) {
            MedicationAdministration medication = (MedicationAdministration) entry.getResource();
            if (medication != null) {
                meds.add(medication);
            }
        }
        return meds;
    }

    public List<MedicationRequest> searchMedOrderByIdentifier(String system, String code) {
        Identifier identifier = FhirUtil.createIdentifier(system, code);
        return searchResourcesByIdentifier(identifier, MedicationRequest.class);
    }

    public void updateMedicationAdministration(MedicationAdministration medAdmin) {
        updateResource(medAdmin);
    }

    public void updateMedicationOrder(MedicationRequest medOrder) {
        updateResource(medOrder);
    }

    public MedicationAdministration createMedicationAdministration(MedicationAdministration medAdmin) {
        return createResource(medAdmin);
    }

    public MedicationRequest createMedicationOrder(MedicationRequest medOrder) {
        return createResource(medOrder);
    }

    public MethodOutcome createMedicationAdministrationIfNotExist(MedicationAdministration medAdmin, Identifier identifier) {
        return createResourceIfNotExist(medAdmin, identifier);
    }

    public MethodOutcome createMedicationOrderIfNotExist(MedicationRequest medOrder, Identifier identifier) {
        return createResourceIfNotExist(medOrder, identifier);
    }

    public List<MedicationAdministration> searchMedicationAdministrationsForPatient(Patient patient) {
        List<MedicationAdministration> results = searchResourcesForPatient(patient, MedicationAdministration.class);
        Collections.sort(results, Comparators.MED_ADMIN_EFFECTIVE_TIME);
        return results;
    }

    public List<MedicationRequest> searchMedicationOrdersForPatient(Patient patient) {
        List<MedicationRequest> results = searchResourcesForPatient(patient, MedicationRequest.class);
        Collections.sort(results, Comparators.MED_ORDER_DATE_WRITTEN);
        return results;
    }

}
