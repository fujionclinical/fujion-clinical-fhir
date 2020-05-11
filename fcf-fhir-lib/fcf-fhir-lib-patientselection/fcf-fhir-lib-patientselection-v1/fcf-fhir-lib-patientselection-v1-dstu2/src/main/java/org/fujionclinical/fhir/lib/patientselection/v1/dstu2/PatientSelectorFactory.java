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
package org.fujionclinical.fhir.lib.patientselection.v1.dstu2;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.fujion.ancillary.IResponseCallback;
import org.fujion.component.Window;
import org.fujion.page.PageUtil;
import org.fujionclinical.fhir.lib.patientselection.common.Constants;
import org.fujionclinical.fhir.lib.patientselection.dstu2.IPatientSelector;
import org.fujionclinical.fhir.lib.patientselection.dstu2.PatientSelectorFactoryBase;
import org.fujionclinical.fhir.lib.patientselection.v1.common.PatientSelectionUtil;

/**
 * This is the patient selection factory.
 */
public class PatientSelectorFactory extends PatientSelectorFactoryBase {

    public static class PatientSelector implements IPatientSelector {

        private final Window dlg = PatientSelectionUtil.createSelectionDialog();

        @Override
        public void select(IResponseCallback<Patient> callback) {
            dlg.modal(callback == null ? null : (event) -> callback.onComplete(dlg.getAttribute(Constants.SELECTED_PATIENT_ATTRIB, Patient.class)));
        }
    }

    protected PatientSelectorFactory() {
        super("New patient selector", PatientSelector.class);
    }
}
