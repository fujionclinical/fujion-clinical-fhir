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
package org.fujionclinical.fhir.scenario.r5;

import org.fujionclinical.api.encounter.EncounterContext;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.api.patient.PatientContext;
import org.fujionclinical.fhir.api.r5.common.BaseService;
import org.fujionclinical.fhir.api.r5.common.FhirUtilR5;
import org.fujionclinical.fhir.api.r5.encounter.EncounterWrapper;
import org.fujionclinical.fhir.api.r5.patient.PatientWrapper;
import org.fujionclinical.fhir.scenario.common.ScenarioBase;
import org.fujionclinical.fhir.scenario.common.ScenarioFactory;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class Scenario extends ScenarioBase<ListResource> {

    private final BaseService fhirService;

    public Scenario(ScenarioFactory<Scenario> scenarioFactory) {
        super(scenarioFactory);
        this.fhirService = (BaseService) scenarioFactory.fhirService;
    }

    @Override
    protected void activate() {
        IBaseResource activationResource = getActivationResource();

        if (activationResource instanceof Encounter) {
            EncounterContext.changeEncounter(EncounterWrapper.wrap((Encounter) activationResource));
        } else if (activationResource instanceof Patient) {
            PatientContext.changePatient(PatientWrapper.wrap((Patient) activationResource));
        }
    }

    @Override
    protected ListResource _loadResources(Consumer<IBaseResource> resources) {
        ListResource list = null;

        try {
            list = (ListResource) fhirService.getResource(getId());

            for (ListResource.ListResourceEntryComponent entry : list.getEntry()) {
                try {
                    IBaseResource resource = fhirService.getResource(entry.getItem());
                    addToPatientList(resource);
                    resources.accept(resource);
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
        return FhirUtilR5.getEntries((Bundle) bundle, IBaseResource.class);
    }

    @Override
    protected IBaseResource _createOrUpdateResource(IBaseResource resource) {
        return fhirService.createOrUpdateResource(resource);
    }
  
    @Override
    protected IPatient toPatient(IBaseResource resource) {
        return resource instanceof Patient ? PatientWrapper.wrap((Patient) resource) : null;
    }
}
