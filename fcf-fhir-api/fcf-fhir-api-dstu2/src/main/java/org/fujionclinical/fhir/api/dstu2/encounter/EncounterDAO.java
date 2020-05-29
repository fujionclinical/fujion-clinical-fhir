package org.fujionclinical.fhir.api.dstu2.encounter;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.fhir.api.common.core.AbstractFhirService;
import org.fujionclinical.fhir.api.dstu2.common.BaseResourceDAO;

public class EncounterDAO extends BaseResourceDAO<IEncounter, Encounter> {

    public EncounterDAO(AbstractFhirService fhirService) {
        super(fhirService, IEncounter.class, Encounter.class);
    }

    @Override
    protected IEncounter convert(Encounter resource) {
        return EncounterWrapper.wrap(resource);
    }

    @Override
    protected Encounter convert(IEncounter domainResource) {
        return EncounterWrapper.unwrap(domainResource);
    }

}
