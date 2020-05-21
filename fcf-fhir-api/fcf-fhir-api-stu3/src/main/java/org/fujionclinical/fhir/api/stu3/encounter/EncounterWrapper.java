package org.fujionclinical.fhir.api.stu3.encounter;

import org.fujion.common.DateRange;
import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IPerson;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.hl7.fhir.dstu3.model.Encounter;

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
        return getNative().hasStatus() ? getNative().getStatus().toCode() : null;
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
