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
package org.fujionclinical.fhir.scenario.dstu2;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ListResource;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import edu.utah.kmm.model.cool.foundation.core.Identifiable;
import edu.utah.kmm.model.cool.foundation.entity.Person;
import edu.utah.kmm.model.cool.mediator.fhir.dstu2.common.Dstu2Utils;
import edu.utah.kmm.model.cool.mediator.fhir.dstu2.common.FhirDataSource;
import edu.utah.kmm.model.cool.mediator.fhir.dstu2.encounter.EncounterTransform;
import edu.utah.kmm.model.cool.mediator.fhir.dstu2.patient.PatientTransform;
import org.fujionclinical.fhir.scenario.common.ScenarioBase;
import org.fujionclinical.fhir.scenario.common.ScenarioFactory;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class Scenario extends ScenarioBase<ListResource> {

    private final FhirDataSource dataSource;

    public Scenario(ScenarioFactory<Scenario> scenarioFactory) {
        super(scenarioFactory);
        this.dataSource = (FhirDataSource) scenarioFactory.dataSource;
    }

    @Override
    protected Identifiable _toDomainObject(IBaseResource activationResource) {
        if (activationResource instanceof Encounter) {
            return EncounterTransform.getInstance().toLogicalModel((Encounter) activationResource);
        } else if (activationResource instanceof Patient) {
            return PatientTransform.getInstance().toLogicalModel((Patient) activationResource);
        } else {
            return null;
        }
    }

    @Override
    protected ListResource _loadResources(Consumer<IBaseResource> resources) {
        ListResource list = null;

        try {
            list = (ListResource) dataSource.getResource(getId());

            for (ListResource.Entry entry : list.getEntry()) {
                try {
                    IBaseResource resource = dataSource.getResource(entry.getItem());
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
        dataSource.deleteResource(resource);
    }

    @Override
    protected ListResource _packageResources(Collection<IBaseResource> resources) {
        ListResource list = new ListResource();

        for (IBaseResource resource : resources) {
            ResourceReferenceDt ref = new ResourceReferenceDt(resource.getIdElement());
            list.addEntry().setItem(ref);
        }

        return list;
    }

    @Override
    protected List<IBaseResource> _relatedResources(IBaseResource resource) {
        return dataSource.everything(resource);
    }

    @Override
    protected List<IBaseResource> _getEntries(IBaseBundle bundle) {
        return Dstu2Utils.getEntries((Bundle) bundle, IBaseResource.class);
    }

    @Override
    protected IBaseResource _createOrUpdateResource(IBaseResource resource) {
        return dataSource.createOrUpdateResource(resource);
    }

    @Override
    protected Person _toPatient(IBaseResource resource) {
        return resource instanceof Patient ? PatientTransform.getInstance().toLogicalModel((Patient) resource).getActor() : null;
    }

}
