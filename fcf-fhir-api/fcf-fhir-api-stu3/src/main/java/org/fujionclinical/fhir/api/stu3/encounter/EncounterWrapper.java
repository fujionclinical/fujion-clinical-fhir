package org.fujionclinical.fhir.api.stu3.encounter;

import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.stu3.common.PeriodWrapper;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;

public class EncounterWrapper extends ResourceWrapper<Encounter> implements IEncounter {

    public static EncounterWrapper create(Encounter encounter) {
        return encounter == null ? null : new EncounterWrapper(encounter);
    }

    private PeriodWrapper period;

    private EncounterWrapper(Encounter encounter) {
        super(encounter);
        period = PeriodWrapper.create(encounter.getPeriod());
    }

    @Override
    public IPeriod getPeriod() {
        return period;
    }

    @Override
    public IEncounter setPeriod(IPeriod period) {
        if (period == null) {
            this.period = null;
            getNative().setPeriod(null);
        } else {
            Period periodDt = PeriodWrapper.unwrap(period);
            getWrapped().setPeriod(periodDt);
            this.period = PeriodWrapper.create(periodDt);
        }

        return this;
    }

    @Override
    public EncounterStatus getStatus() {
        return FhirUtil.convertEnum(getWrapped().getStatus(), EncounterStatus.class);
    }

    @Override
    public ILocation getLocation() {
        return null;
    }

}
