package org.fujionclinical.fhir.api.r5.encounter;

import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.r5.common.ConceptWrapper;
import org.fujionclinical.fhir.api.r5.common.PeriodWrapper;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.Period;

import java.util.List;

public class EncounterWrapper extends ResourceWrapper<Encounter> implements IEncounter {

    public static EncounterWrapper wrap(Encounter encounter) {
        return encounter == null ? null : new EncounterWrapper(encounter);
    }

    private PeriodWrapper period;

    private final List<IConcept> types;
    
    private EncounterWrapper(Encounter encounter) {
        super(encounter);
        period = PeriodWrapper.wrap(encounter.getPeriod());
        types = ConceptWrapper.wrap(encounter.getType());
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
            Period fhirPeriod = PeriodWrapper.unwrap(period);
            getWrapped().setPeriod(fhirPeriod);
            this.period = PeriodWrapper.wrap(fhirPeriod);
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

    @Override
    public List<IConcept> getTypes() {
        return types;
    }

}
