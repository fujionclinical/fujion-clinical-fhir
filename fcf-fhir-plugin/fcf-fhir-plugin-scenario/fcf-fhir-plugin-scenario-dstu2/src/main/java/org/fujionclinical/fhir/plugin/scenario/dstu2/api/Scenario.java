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
package org.fujionclinical.fhir.plugin.scenario.dstu2.api;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.ListResource;
import ca.uhn.fhir.model.primitive.IdDt;
import org.fujionclinical.fhir.dstu2.api.common.BaseService;
import org.fujionclinical.fhir.dstu2.api.common.FhirUtil;
import org.fujionclinical.fhir.plugin.scenario.common.ScenarioBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class Scenario extends ScenarioBase {

    private final BaseService fhirService;

    public Scenario(Resource scenarioYaml, BaseService fhirService) {
        super(scenarioYaml, fhirService.getClient().getFhirContext());
        this.fhirService = fhirService;
    }

    @Override
    protected IIdType _createScenarioId(String resourceType, String id) {
        return new IdDt(resourceType, id);
    }

    @Override
    protected void _loadResources(Consumer<IBaseResource> resources) {
        try {
            ListResource list = (ListResource) fhirService.getResource(getId());
            resources.accept(list);

            for (ListResource.Entry entry : list.getEntry()) {
                try {
                    resources.accept(fhirService.getResource(entry.getItem()));
                } catch (Exception e) {}
            }

        } catch (Exception e) {}
    }

    @Override
    protected void _deleteResource(IBaseResource resource) {
        fhirService.deleteResource(resource);
    }

    @Override
    protected IBaseResource _packageResources(Collection<IBaseResource> resources) {
        ListResource list = new ListResource();

        for (IBaseResource resource: resources) {
            ResourceReferenceDt ref = new ResourceReferenceDt(resource.getIdElement());
            list.addEntry().setItem(ref);
        }

        return list;
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
