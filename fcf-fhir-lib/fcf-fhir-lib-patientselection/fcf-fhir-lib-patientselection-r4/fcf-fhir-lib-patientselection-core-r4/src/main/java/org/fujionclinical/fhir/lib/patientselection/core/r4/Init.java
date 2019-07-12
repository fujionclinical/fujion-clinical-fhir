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

import org.fujion.event.EventUtil;
import org.fujion.event.IEventListener;
import org.fujionclinical.fhir.r4.api.patient.PatientContext;
import org.fujionclinical.shell.IShellStartup;
import org.fujionclinical.shell.Shell;
import org.fujionclinical.shell.ShellUtil;
import org.fujionclinical.ui.action.ActionRegistry;
import org.fujionclinical.ui.action.IAction;
import org.fujionclinical.ui.command.CommandUtil;

/**
 * Patient selection initializers.
 */
public class Init implements IShellStartup {

    private static final String FORCE_SELECT_EVENT = "force.patient.selection";

    private static final String ACTION_ID = "patientselection.select";

    private static final String ACTION_NAME = "@patientselection.action.select.label";

    private static final String ACTION_SCRIPT = "groovy:" + PatientSelection.class.getName() + ".show(true, null);";

    private static final IAction PATIENT_SELECT = ActionRegistry.register(true, ACTION_ID, ACTION_NAME, ACTION_SCRIPT);

    private static final IEventListener forceSelectionListener = (event) -> PatientSelection.show(true);
    
    @Override
    public boolean execute() {
        Shell shell = ShellUtil.getShell();
        CommandUtil.associateCommand("PATIENT_SELECT", shell, PATIENT_SELECT);
        
        // call the patient selection routine at login, if the user preference is set
        if (PatientContext.getActivePatient() == null && PatientSelection.forcePatientSelection()) {
            shell.addEventListener(FORCE_SELECT_EVENT, forceSelectionListener);
            EventUtil.post(FORCE_SELECT_EVENT, shell, null);
        }
        
        return true;
    }
    
}
