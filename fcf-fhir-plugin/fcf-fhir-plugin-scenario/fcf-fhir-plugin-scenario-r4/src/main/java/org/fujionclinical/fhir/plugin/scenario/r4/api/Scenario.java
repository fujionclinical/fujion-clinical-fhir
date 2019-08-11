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
package org.fujionclinical.fhir.plugin.scenario.r4.api;

import org.fujionclinical.fhir.plugin.scenario.common.ScenarioBase;
import org.fujionclinical.fhir.r4.api.common.BaseService;
import org.fujionclinical.fhir.r4.api.common.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Set;

public class Scenario extends ScenarioBase {

    private final BaseService fhirService;

    public Scenario(Resource scenarioYaml, BaseService fhirService) {
        super(scenarioYaml, fhirService.getClient().getFhirContext());
        this.fhirService = fhirService;
    }

    @Override
    protected List<? extends IBaseResource> _searchResourcesByTag(IBaseCoding tag, Class<? extends IBaseResource> clazz) {
        return fhirService.searchResourcesByTag(tag, clazz);
    }

    @Override
    protected Set<Class<? extends IBaseResource>> _getResourceClasses() {
        return ScenarioUtil.getResourceClasses();
    }

    @Override
    protected void _deleteResource(IBaseResource resource) {
        fhirService.deleteResource(resource);
    }

    @Override
    protected List<IBaseResource> _relatedResources(IBaseResource resource) {
        return fhirService.everything(resource);
    }

    @Override
    protected List<IBaseResource> _getEntries(IBaseBundle bundle) {
        return FhirUtil.getEntries((Bundle) bundle, IBaseResource.class);
    }

    @Override
    protected IBaseResource _createOrUpdateResource(IBaseResource resource) {
        return fhirService.createOrUpdateResource(resource);
    }

}
