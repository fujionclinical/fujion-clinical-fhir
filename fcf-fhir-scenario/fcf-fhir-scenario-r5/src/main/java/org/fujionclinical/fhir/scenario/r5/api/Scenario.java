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

import org.fujionclinical.fhir.r5.api.common.BaseService;
import org.fujionclinical.fhir.r5.api.common.FhirUtil;
import org.fujionclinical.fhir.scenario.common.ScenarioBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.ListResource;
import org.hl7.fhir.r5.model.Reference;
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class Scenario extends ScenarioBase<ListResource> {

    private final BaseService fhirService;

    public Scenario(
            Resource scenarioYaml,
            BaseService fhirService) {
        super(scenarioYaml, fhirService.getClient().getFhirContext());
        this.fhirService = fhirService;
    }

    @Override
    protected ListResource _loadResources(Consumer<IBaseResource> resources) {
        ListResource list = null;

        try {
            list = (ListResource) fhirService.getResource(getId());

            for (ListResource.ListResourceEntryComponent entry : list.getEntry()) {
                try {
                    resources.accept(fhirService.getResource(entry.getItem()));
                } catch (Exception e) {
                    // NOP
                }
            }

        } catch (Exception e) {
            // NOP
        }

        return list;
    }

    @Override
    protected void _deleteResource(IBaseResource resource) {
        fhirService.deleteResource(resource);
    }

    @Override
    protected ListResource _packageResources(Collection<IBaseResource> resources) {
        ListResource list = new ListResource();

        for (IBaseResource resource : resources) {
            Reference ref = new Reference(resource.getIdElement());
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
