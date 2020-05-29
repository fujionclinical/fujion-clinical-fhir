package org.fujionclinical.fhir.api.r5.encounter;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.fhir.api.r5.common.BaseResourceDAO;
import org.hl7.fhir.r5.model.Encounter;

public class EncounterDAO extends BaseResourceDAO<IEncounter, Encounter> {

    public EncounterDAO(IGenericClient fhirClient) {
        super(fhirClient, IEncounter.class, Encounter.class);
    }

    @Override
    protected IEncounter wrapResource(Encounter resource) {
        return EncounterWrapper.wrap(resource);
    }
}
