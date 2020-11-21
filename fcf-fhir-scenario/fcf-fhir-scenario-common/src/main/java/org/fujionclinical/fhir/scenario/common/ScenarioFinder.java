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

import edu.utah.kmm.model.cool.mediator.fhir.core.AbstractFhirDataSource;
import org.fujion.common.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Registry that detects and automatically registers scenario definitions.
 */
public class ScenarioFinder<SCENARIO extends ScenarioBase>
        implements ApplicationContextAware, Iterable<ScenarioFactory<SCENARIO>> {

    private static final Logger log = Logger.create(ScenarioFinder.class);

    private final String scenarioBase;

    private final Class<SCENARIO> scenarioClass;

    private final AbstractFhirDataSource dataSource;

    private final Map<String, ScenarioFactory<SCENARIO>> scenarioFactories = new HashMap<>();

    public ScenarioFinder(
            Class<SCENARIO> scenarioClass,
            String scenarioBase,
            AbstractFhirDataSource dataSource) {
        this.scenarioClass = scenarioClass;
        this.scenarioBase = scenarioBase;
        this.dataSource = dataSource;
    }

    public ScenarioFactory<SCENARIO> getScenarioFactory(String name) {
        return scenarioFactories.get(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            for (Resource yaml : applicationContext.getResources(scenarioBase + "/*.yaml")) {
                ScenarioFactory<SCENARIO> factory = new ScenarioFactory(scenarioClass, yaml, dataSource);
                scenarioFactories.put(factory.getName(), factory);
            }
        } catch (Exception e) {
            log.warn("Error loading scenarios from folder " + scenarioBase + ":\n" + e.getMessage());
        }
    }

    @Override
    public Iterator<ScenarioFactory<SCENARIO>> iterator() {
        return scenarioFactories.values().iterator();
    }

}
