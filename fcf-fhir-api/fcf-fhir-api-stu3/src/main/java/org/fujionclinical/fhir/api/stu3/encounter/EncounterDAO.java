package org.fujionclinical.fhir.api.stu3.encounter;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.fhir.api.stu3.common.BaseResourceDAO;
import org.hl7.fhir.dstu3.model.Encounter;

public class EncounterDAO extends BaseResourceDAO<IEncounter, Encounter> {

    public EncounterDAO(IGenericClient fhirClient) {
        super(fhirClient, IEncounter.class, Encounter.class);
    }

    @Override
    protected IEncounter wrapResource(Encounter resource) {
        return EncounterWrapper.wrap(resource);
    }
}
