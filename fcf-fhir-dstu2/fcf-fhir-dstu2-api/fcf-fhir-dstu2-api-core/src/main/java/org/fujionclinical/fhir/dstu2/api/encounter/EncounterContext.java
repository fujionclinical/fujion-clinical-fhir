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
package org.fujionclinical.fhir.dstu2.api.encounter;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.context.ContextItems;
import org.fujionclinical.api.context.ContextManager;
import org.fujionclinical.api.context.IContextEvent;
import org.fujionclinical.api.context.ISurveyResponse;
import org.fujionclinical.fhir.dstu2.api.common.ResourceContext;
import org.fujionclinical.fhir.dstu2.api.patient.PatientContext;

/**
 * Wrapper for shared encounter context.
 */
public class EncounterContext extends ResourceContext<Encounter> implements PatientContext.IPatientContextEvent {
    
    private static final Log log = LogFactory.getLog(EncounterContext.class);
    
    private static final String SUBJECT_NAME = "Encounter";
    
    public interface IEncounterContextEvent extends IContextEvent {};
    
    /**
     * Returns the managed encounter context.
     *
     * @return Encounter context.
     */
    static public EncounterContext getEncounterContext() {
        return (EncounterContext) ContextManager.getInstance().getSharedContext(EncounterContext.class.getName());
    }
    
    /**
     * Returns the current encounter from the shared context.
     *
     * @return Current encounter.
     */
    public static Encounter getActiveEncounter() {
        return getEncounterContext().getContextObject(false);
    }
    
    /**
     * Requests a context change to the specified encounter.
     *
     * @param encounter The encounter.
     */
    public static void changeEncounter(Encounter encounter) {
        try {
            getEncounterContext().requestContextChange(encounter);
        } catch (Exception e) {
            log.error("Error during request context change.", e);
        }
    }
    
    /**
     * Request an encounter context change.
     *
     * @param logicalId Logical id of the encounter.
     */
    public static void changeEncounter(String logicalId) {
        getEncounterContext().requestContextChange(logicalId);
    }
    
    /**
     * Creates the context wrapper and registers its context change callback interface.
     */
    public EncounterContext() {
        this(null);
    }
    
    /**
     * Creates the context wrapper and registers its context change callback interface.
     *
     * @param encounter Initial value for context.
     */
    public EncounterContext(Encounter encounter) {
        super(SUBJECT_NAME, Encounter.class, IEncounterContextEvent.class, encounter);
    }
    
    /**
     * Commits or rejects the pending context change.
     *
     * @param accept If true, the pending change is committed. If false, the pending change is
     *            canceled.
     */
    @Override
    public void commit(boolean accept) {
        super.commit(accept);
    }
    
    /**
     * Creates a CCOW context from the specified encounter object.
     */
    @Override
    protected ContextItems toCCOWContext(Encounter encounter) {
        //TODO: contextItems.setItem(...);
        return contextItems;
    }
    
    /**
     * Returns an encounter instance based on the specified CCOW context.
     */
    @Override
    protected Encounter fromCCOWContext(ContextItems contextItems) {
        Encounter encounter = null;
        
        try {
            encounter = new Encounter();
            //TODO: Populate encounter object from context items.
            return encounter;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
    
    /**
     * Returns a priority value of 5.
     *
     * @return Priority value for context manager.
     */
    @Override
    public int getPriority() {
        return 5;
    }
    
    // IPatientContextEvent
    
    @Override
    public void canceled() {
    }
    
    @Override
    public void committed() {
    }
    
    @Override
    public void pending(ISurveyResponse response) {
        //Patient patient = PatientContext.getPatientContext().getContextObject(true);
        changeEncounter((Encounter) null);
        //changeEncounter(EncounterUtil.getDefaultEncounter(patient));
    }
}
