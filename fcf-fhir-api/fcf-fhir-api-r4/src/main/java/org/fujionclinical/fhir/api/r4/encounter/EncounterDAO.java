package org.fujionclinical.fhir.api.r4.encounter;

import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.fhir.api.common.core.AbstractFhirService;
import org.fujionclinical.fhir.api.r4.common.BaseResourceDAO;
import org.hl7.fhir.r4.model.Encounter;

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
