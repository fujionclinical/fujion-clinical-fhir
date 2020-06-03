/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.fhir.api.stu3.encounter;

import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.api.location.ILocation;
import org.fujionclinical.api.model.IConcept;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.stu3.common.ConceptWrapper;
import org.fujionclinical.fhir.api.stu3.common.FhirUtilStu3;
import org.fujionclinical.fhir.api.stu3.common.PeriodWrapper;
import org.fujionclinical.fhir.api.stu3.patient.PatientWrapper;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
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
        patient = PatientWrapper.wrap(FhirUtilStu3.getFhirService().getResource(patientRef, Patient.class));
    }

    @Override
    public IPatient getPatient() {
        return patient;
    }

    @Override
    public void setPatient(IPatient patient) {
        Patient pat = PatientWrapper.unwrap(patient);
        patientRef.setResource(pat);
        initPatientWrapper();
    }

    @Override
    public IPeriod getPeriod() {
        return period;
    }

    @Override
    public void setPeriod(IPeriod period) {
        if (period == null) {
            this.period = null;
            getWrapped().setPeriod(null);
        } else {
            Period per = PeriodWrapper.unwrap(period);
            getWrapped().setPeriod(per);
            this.period = PeriodWrapper.wrap(per);
        }
    }

    @Override
    public EncounterStatus getStatus() {
        return FhirUtil.convertEnum(getWrapped().getStatus(), EncounterStatus.class);
    }

    @Override
    public List<ILocation> getLocations() {
        return null;
    }

    @Override
    public List<IConcept> getTypes() {
        return types;
    }

}
