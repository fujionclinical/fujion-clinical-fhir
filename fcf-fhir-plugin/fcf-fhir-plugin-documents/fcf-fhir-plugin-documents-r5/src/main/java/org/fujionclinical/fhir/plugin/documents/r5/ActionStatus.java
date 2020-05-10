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
package org.fujionclinical.fhir.plugin.documents.r5;

import org.fujionclinical.api.context.ISurveyResponse;
import org.fujionclinical.fhir.api.r5.patient.PatientContext;
import org.fujionclinical.fhir.api.r5.patient.PatientContext.IPatientContextEvent;
import org.fujionclinical.shell.plugins.PluginStatus;

/**
 * Updates the enabled status of the plugin.
 */
public class ActionStatus extends PluginStatus implements IPatientContextEvent {
    
    /**
     * Returns true if there is no current patient or the current patient has no documents.
     */
    @Override
    public boolean checkDisabled() {
        return PatientContext.getActivePatient() == null;
    }
    
    /**
     * @see org.fujionclinical.api.context.IContextEvent#canceled()
     */
    @Override
    public void canceled() {
    }
    
    /**
     * Update the plugin enabled status when the patient selection changes.
     */
    @Override
    public void committed() {
        updateDisabled();
    }
    
    /**
     * @see org.fujionclinical.api.context.IContextEvent#pending(ISurveyResponse)
     */
    @Override
    public void pending(ISurveyResponse response) {
        response.accept();
    }
    
}
