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
package org.fujionclinical.fhir.scenario.r5.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.context.ContextItems;
import org.fujionclinical.api.context.ContextManager;
import org.fujionclinical.api.context.IContextEvent;
import org.fujionclinical.api.context.ManagedContext;

/**
 * Wrapper for shared scenario context.
 */
public class ScenarioContext extends ManagedContext<Scenario> {
    
    private static final String SUBJECT_NAME = "Scenario";
    
    private static final Log log = LogFactory.getLog(ScenarioContext.class);
    
    public interface IScenarioContextEvent extends IContextEvent {}

    private final ScenarioRegistry registry;
    
    /**
     * Returns the managed scenario context.
     *
     * @return Scenario context.
     */
    public static ScenarioContext getScenarioContext() {
        return (ScenarioContext) ContextManager.getInstance().getSharedContext(ScenarioContext.class.getName());
    }
    
    /**
     * Request a scenario context change.
     *
     * @param scenario New scenario.
     */
    public static void changeScenario(Scenario scenario) {
        try {
            getScenarioContext().requestContextChange(scenario);
        } catch (Exception e) {
            log.error("Error during scenario context change.", e);
        }
    }
    
    /**
     * Request a scenario context change.
     *
     * @param name Name of the scenario.
     */
    public static void changeScenario(String name) {
        ScenarioContext ctx = getScenarioContext();
        ctx.requestContextChange(ctx.registry.get(name));
    }
    
    /**
     * Returns the scenario in the current context.
     *
     * @return Scenario object (may be null).
     */
    public static Scenario getActiveScenario() {
        return getScenarioContext().getContextObject(false);
    }
    
    /**
     * Create a shared scenario context with an initial null state.
     *
     * @param registry Scenario registry for lookups by name.
     */
    public ScenarioContext(ScenarioRegistry registry) {
        super(SUBJECT_NAME, IScenarioContextEvent.class);
        this.registry = registry;
    }
    
    /**
     * Not implemented
     */
    @Override
    public ContextItems toCCOWContext(Scenario scenario) {
        return null;
    }
    
    /**
     * Not implemented
     */
    @Override
    public Scenario fromCCOWContext(ContextItems contextItems) {
        return null;
    }
    
}
