/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2019 fujionclinical.org
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
package org.fujionclinical.fhir.smart.dstu2;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.fujionclinical.fhir.dstu2.api.patient.PatientContext;
import org.fujionclinical.fhir.smart.common.SmartContextBase;

/**
 * Implements SMART patient context.
 */
public class SmartContextPatient extends SmartContextBase {


    /**
     * Binds patient context changes to the SMART patient context.
     */
    public SmartContextPatient() {
        super("patient", "CONTEXT.CHANGED.Patient");
    }

    /**
     * Populate context map with information about currently selected patient.
     *
     * @param context Context map to be populated.
     */
    @Override
    protected void updateContext(ContextMap context) {
        Patient patient = PatientContext.getActivePatient();

        if (patient != null) {
            context.put("patient", patient.getIdElement().getIdPart());
        }
    }

}
