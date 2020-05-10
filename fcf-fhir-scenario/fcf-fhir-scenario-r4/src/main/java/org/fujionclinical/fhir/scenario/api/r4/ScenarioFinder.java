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
package org.fujionclinical.fhir.scenario.api.r4;

import org.fujion.common.Logger;
import org.fujionclinical.fhir.api.r4.common.BaseService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * Registry that detects and automatically registers scenario definitions.
 */
public class ScenarioFinder implements ApplicationContextAware {

    private static final Logger log = Logger.create(ScenarioFinder.class);

    private final String scenarioBase;

    private final BaseService fhirService;

    private final ScenarioRegistry scenarioRegistry;

    public ScenarioFinder(String scenarioBase, ScenarioRegistry scenarioRegistry, BaseService fhirService) {
        this.scenarioBase = scenarioBase;
        this.scenarioRegistry = scenarioRegistry;
        this.fhirService = fhirService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            for (Resource yaml : applicationContext.getResources(scenarioBase + "/*.yaml")) {
                scenarioRegistry.register(new Scenario(yaml, fhirService));
            }
        } catch (Exception e) {
            log.warn("Error loading scenarios from folder " + scenarioBase + ":\n" + e.getMessage());
        }
    }
}
