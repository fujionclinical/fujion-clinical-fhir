package org.fujionclinical.fhir.api.dstu2.encounter;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.fhir.api.dstu2.common.ResourceFactory;

public class EncounterFactory extends ResourceFactory<IEncounter, Encounter> {

    public EncounterFactory(IGenericClient fhirClient) {
        super(fhirClient, IEncounter.class, Encounter.class);
    }

    @Override
    protected IEncounter wrapResource(Encounter resource) {
        return new EncounterWrapper(resource);
    }
}
