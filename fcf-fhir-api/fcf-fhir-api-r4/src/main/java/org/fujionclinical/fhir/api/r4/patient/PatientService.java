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
package org.fujionclinical.fhir.api.r4.patient;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import edu.utah.kmm.model.cool.mediator.fhir.r4.common.BaseFhirService;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

public class PatientService extends BaseFhirService {

    public PatientService(IGenericClient client) {
        super(client);
    }

    public Patient updatePatient(Patient patient) {
        return updateResource(patient);
    }

    public Patient createPatient(Patient patient) {
        return createResource(patient);
    }

    public MethodOutcome addPatientIfNotExist(
            Patient patient,
            Identifier identifier) {
        return createResourceIfNotExist(patient, identifier);
    }

}
