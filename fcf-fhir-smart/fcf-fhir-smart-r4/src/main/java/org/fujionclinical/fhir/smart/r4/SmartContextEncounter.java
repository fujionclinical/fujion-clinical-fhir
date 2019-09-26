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
package org.fujionclinical.fhir.smart.r4;

import org.fujionclinical.fhir.r4.api.encounter.EncounterContext;
import org.fujionclinical.fhir.smart.common.SmartContextBase;
import org.hl7.fhir.r4.model.Encounter;

/**
 * Implements SMART encounter context.
 */
public class SmartContextEncounter extends SmartContextBase {

    /**
     * Binds encounter context changes to the SMART encounter context.
     */
    public SmartContextEncounter() {
        super("encounter", "CONTEXT.CHANGED.Encounter");
    }

    /**
     * Populate context map with information about currently selected encounter.
     *
     * @param context Context map to be populated.
     */
    @Override
    protected void updateContext(ContextMap context) {
        Encounter encounter = EncounterContext.getActiveEncounter();

        if (encounter != null) {
            context.put("encounter", encounter.getIdElement().getIdPart());
        }
    }

}
