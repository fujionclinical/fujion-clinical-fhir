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
package org.fujionclinical.fhir.lib.sharedforms.r4.controller;

import org.fujionclinical.api.FrameworkUtil;
import org.fujionclinical.api.context.ISurveyResponse;
import org.fujionclinical.fhir.r4.api.patient.PatientContext;
import org.fujionclinical.ui.sharedforms.controller.AbstractServiceController;
import org.hl7.fhir.r4.model.Patient;

/**
 * Use with AbstractServiceController subclasses that require an awareness of patient context.
 */
public class PatientQueryParameter extends AbstractServiceController.SupplementalQueryParam<Patient>
    implements PatientContext.IPatientContextEvent {

    public static final String LABEL_ID_NO_PATIENT = "%.plugin.patient.selection.required";

    public PatientQueryParameter() {
        super("patient", PatientContext.getActivePatient());
        FrameworkUtil.getAppFramework().registerObject(this);
    }

    @Override
    protected String hasRequired() {
        return getValue() == null ? LABEL_ID_NO_PATIENT : null;
    }

    @Override
    protected void destroy() {
        FrameworkUtil.getAppFramework().unregisterObject(this);
        super.destroy();
    }

    @Override
    public void pending(ISurveyResponse response) {
        response.accept();
    }

    @Override
    public void committed() {
        setValue(PatientContext.getActivePatient());
    }

    @Override
    public void canceled() {
        // ignored
    }
}
