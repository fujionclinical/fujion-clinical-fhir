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
package org.fujionclinical.fhir.api.r4.test;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import org.fujionclinical.fhir.api.common.client.FhirContext;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientTest {

    private static final String FHIR_EP = "TEST_FHIR_R4_EP";

    @Test
    public void testClient() {
        String endpoint = System.getenv(FHIR_EP);

        if (endpoint == null) {
            System.err.println("Environment variable " + FHIR_EP + " was not found; skipping test...");
            return;
        }

        FhirContext ctx = new FhirContext(FhirVersionEnum.R4);
        IGenericClient client = ctx.newRestfulGenericClient(endpoint);
        //client.registerInterceptor(new BasicAuthInterceptor("user123", "user321$"));
        ((GenericClient) client).setDontValidateConformance(true);
        Bundle bundle = client.search().forResource(Patient.class).count(1).returnBundle(Bundle.class).execute();
        assertTrue("No patient resources returned.", bundle.getEntry().size() > 0);
        Patient patient = (Patient) bundle.getEntryFirstRep().getResource();
        String patient_id = patient.getIdElement().getIdPart();
        Patient patient1 = client.read().resource(Patient.class).withId(patient_id).execute();
        assertEquals(patient_id, patient1.getIdElement().getIdPart());
        bundle = client.search().byUrl("Patient?_id=" + patient_id).returnBundle(Bundle.class).execute();
        assertEquals(1, bundle.getEntry().size());
        Patient patient2 = (Patient) bundle.getEntry().get(0).getResource();
        assertEquals(patient_id, patient2.getIdElement().getIdPart());
    }

}
