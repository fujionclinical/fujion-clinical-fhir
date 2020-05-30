package org.fujionclinical.fhir.api.r5.encounter;

import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.r5.common.ConceptWrapper;
import org.fujionclinical.fhir.api.r5.common.FhirUtilR5;
import org.fujionclinical.fhir.api.r5.common.PeriodWrapper;
import org.fujionclinical.fhir.api.r5.patient.PatientWrapper;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Period;
import org.hl7.fhir.r5.model.Reference;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class EncounterWrapper extends ResourceWrapper<Encounter> implements IEncounter {

    private final List<IConcept> types;

    private final Reference patientRef;

    private PeriodWrapper period;

    private PatientWrapper patient;

    public static EncounterWrapper wrap(Encounter encounter) {
        return encounter == null ? null : new EncounterWrapper(encounter);
    }

    public static Encounter unwrap(IEncounter encounter) {
        if (encounter == null) {
            return null;
        }

        if (encounter instanceof EncounterWrapper) {
            return ((EncounterWrapper) encounter).getWrapped();
        }

        EncounterWrapper enc = wrap(new Encounter());
        BeanUtils.copyProperties(encounter, enc);
        return enc.getWrapped();
    }

    private EncounterWrapper(Encounter encounter) {
        super(encounter);
        period = PeriodWrapper.wrap(encounter.getPeriod());
        types = ConceptWrapper.wrap(encounter.getType());
        patientRef = getWrapped().getSubject();
        initPatientWrapper();
    }

    private void initPatientWrapper() {
        patient = PatientWrapper.wrap(FhirUtilR5.getFhirService().getResource(patientRef, Patient.class));
    }

    @Override
    public IPatient getPatient() {
        return patient;
    }

    @Override
    public IEncounter setPatient(IPatient patient) {
        Patient pat = PatientWrapper.unwrap(patient);
        patientRef.setResource(pat);
        initPatientWrapper();
        return this;
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
