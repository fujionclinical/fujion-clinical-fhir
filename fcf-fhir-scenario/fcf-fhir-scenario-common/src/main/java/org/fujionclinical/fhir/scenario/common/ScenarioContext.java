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
package org.fujionclinical.fhir.scenario.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.context.ContextItems;
import org.fujionclinical.api.context.ContextManager;
import org.fujionclinical.api.context.IContextSubscriber;
import org.fujionclinical.api.context.ManagedContext;

/**
 * Wrapper for shared scenario context.
 */
@SuppressWarnings("rawtypes unchecked")
public class ScenarioContext<SCENARIO extends ScenarioBase> extends ManagedContext<SCENARIO> {

    public interface IScenarioContextSubscriber extends IContextSubscriber {

    }

    private static final String SUBJECT_NAME = "Scenario";

    private static final Log log = LogFactory.getLog(ScenarioContext.class);

    private final ScenarioRegistry registry;

    /**
     * Returns the managed scenario context.
     *
     * @param <SCENARIO> The scenario type.
     * @return Scenario context.
     */
    public static <SCENARIO extends ScenarioBase> ScenarioContext<SCENARIO> getScenarioContext() {
        return (ScenarioContext) ContextManager.getInstance().getSharedContext(ScenarioContext.class.getName());
    }

    /**
     * Request a scenario context change.
     *
     * @param scenario New scenario.
     * @param <SCENARIO> The scenario type.
     */
    public static <SCENARIO extends ScenarioBase> void changeScenario(SCENARIO scenario) {
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
     * @param <SCENARIO> The scenario type.
     * @return Scenario object (may be null).
     */
    public static <SCENARIO extends ScenarioBase> SCENARIO getActiveScenario() {
        return (SCENARIO) getScenarioContext().getContextObject(false);
    }

    /**
     * Create a shared scenario context with an initial null state.
     *
     * @param registry Scenario registry for lookups by name.
     */
    public ScenarioContext(ScenarioRegistry registry) {
        super(SUBJECT_NAME, IScenarioContextSubscriber.class);
        this.registry = registry;
    }

    @Override
    public void commit(boolean accept) {
        super.commit(accept);
        ScenarioBase scenario = getContextObject(false);

        if (accept && scenario != null) {
            scenario.activate();
        }
    }

    /**
     * Not implemented
     */
    @Override
    public ContextItems toCCOWContext(SCENARIO scenario) {
        return null;
    }

    /**
     * Not implemented
     */
    @Override
    public SCENARIO fromCCOWContext(ContextItems contextItems) {
        return null;
    }

}
