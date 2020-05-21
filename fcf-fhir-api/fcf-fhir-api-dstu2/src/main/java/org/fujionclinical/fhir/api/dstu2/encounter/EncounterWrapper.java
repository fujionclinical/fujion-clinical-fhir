package org.fujionclinical.fhir.api.dstu2.encounter;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.fujion.common.DateRange;
import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IPerson;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;

import java.util.List;

public class EncounterWrapper extends ResourceWrapper<Encounter> implements IEncounter {

    public EncounterWrapper(Encounter encounter) {
        super(encounter);
    }

    @Override
    public DateRange getPeriod() {
        return null;
    }

    @Override
    public String getStatus() {
        return getNative().getStatus();
    }

    @Override
    public List<IPerson> getParticipants() {
        return null;
    }

    @Override
    public ILocation getLocation() {
        return null;
    }
}
