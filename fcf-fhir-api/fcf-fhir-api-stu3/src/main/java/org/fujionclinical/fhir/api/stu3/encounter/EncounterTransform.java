package org.fujionclinical.fhir.api.stu3.encounter;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.hl7.fhir.dstu3.model.Encounter;

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
