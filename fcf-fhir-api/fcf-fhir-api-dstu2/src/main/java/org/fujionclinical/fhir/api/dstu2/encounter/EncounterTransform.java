package org.fujionclinical.fhir.api.dstu2.encounter;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.encounter.IEncounter;

public class EncounterTransform implements IWrapperTransform<IEncounter, Encounter> {

    public static final EncounterTransform instance = new EncounterTransform();

    @Override
    public IEncounter _wrap(Encounter value) {
        return new EncounterWrapper(value);
    }

    @Override
    public Encounter newWrapped() {
        return new Encounter();
    }

}
