package org.fujionclinical.fhir.api.dstu2.encounter;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.springframework.beans.BeanUtils;

public class EncounterTransform implements IWrapperTransform<IEncounter, Encounter> {

    public static final EncounterTransform instance = new EncounterTransform();

    @Override
    public Encounter _unwrap(IEncounter encounter) {
        Encounter enc = new Encounter();
        IEncounter wrapped = _wrap(enc);
        BeanUtils.copyProperties(encounter, wrapped);
        return enc;
    }

    @Override
    public IEncounter _wrap(Encounter value) {
        return new EncounterWrapper(value);
    }

}
