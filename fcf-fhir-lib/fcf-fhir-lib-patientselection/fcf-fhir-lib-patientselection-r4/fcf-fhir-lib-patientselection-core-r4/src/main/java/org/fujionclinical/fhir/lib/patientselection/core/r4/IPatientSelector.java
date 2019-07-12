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
package org.fujionclinical.fhir.lib.patientselection.core.r4;

import org.fujion.ancillary.IResponseCallback;
import org.hl7.fhir.r4.model.Patient;

/**
 * This interface must be implemented by any patient selector.
 */
public interface IPatientSelector {
    
    /**
     * Displays the patient selection dialog.
     * 
     * @param callback Returns the selected patient at the time the dialog was closed. The selected
     *            patient will be null if no patient was selected when the dialog was closed or if
     *            the selection was canceled by the user.
     */
    void select(IResponseCallback<Patient> callback);
}
