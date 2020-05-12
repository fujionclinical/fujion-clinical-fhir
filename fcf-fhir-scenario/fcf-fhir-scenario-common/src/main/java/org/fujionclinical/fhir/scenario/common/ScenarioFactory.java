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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.fujion.common.Logger;
import org.fujion.common.MiscUtil;
import org.fujionclinical.fhir.api.common.core.BaseFhirService;
import org.fujionclinical.fhir.api.common.patientlist.IPatientAdapterFactory;
import org.fujionclinical.fhir.api.common.patientlist.PatientListRegistry;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for a scenario factory.
 */
public class ScenarioFactory<SCENARIO extends ScenarioBase> {

    private static final Logger log = Logger.create(ScenarioFactory.class);

    public final Map<String, Map<String, String>> scenarioConfig;

    public final String scenarioName;

    public final IBaseCoding scenarioTag;

    public final IIdType scenarioId;

    public final BaseFhirService fhirService;

    public final Resource scenarioYaml;

    public final IPatientAdapterFactory patientAdapterFactory;

    private final Class<SCENARIO> scenarioClass;

    public ScenarioFactory(
            Class<SCENARIO> scenarioClass,
            Resource scenarioYaml,
            IPatientAdapterFactory patientAdapterFactory,
            BaseFhirService fhirService) {
        this.scenarioClass = scenarioClass;
        this.scenarioYaml = scenarioYaml;
        this.patientAdapterFactory = patientAdapterFactory;
        this.fhirService = fhirService;

        try (InputStream in = scenarioYaml.getInputStream()) {
            Map<String, ?> config = new Yaml().load(in);
            Map<String, String> meta = (Map<String, String>) getParam(config, "scenario");
            this.scenarioConfig = (Map<String, Map<String, String>>) getParam(config, "resources");
            this.scenarioId = createScenarioId(getParam(meta, "id"));
            this.scenarioName = getParam(meta, "name");
            this.scenarioTag = ScenarioUtil.createScenarioTag(scenarioId.getIdPart(), scenarioName);
        } catch (Exception e) {
            log.error(() -> "Failed to load scenario configuration: " + scenarioYaml, e);
            throw MiscUtil.toUnchecked(e);
        }

        log.info(() -> "Loaded scenario configuration: " + scenarioName);
    }

    public SCENARIO create() {
        try {
            return ConstructorUtils.invokeConstructor(scenarioClass, this);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    /**
     * Creates the id to be used to store scenario resources.
     *
     * @param id The resource id.
     * @return The newly created id.
     */
    private IIdType createScenarioId(String id) {
        return new IdDt("List", id);
    }

    private <T> T getParam(
            Map<String, T> map,
            String param) {
        T value = map.get(param);
        Assert.notNull(value, () -> "Missing configuration parameter: " + param);
        return value;
    }

    /**
     * Returns the name of this scenario.
     *
     * @return The scenario name.
     */
    public String getName() {
        return scenarioName;
    }

}