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
package org.fujionclinical.fhir.api.r5.encounter;

import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.location.ILocation;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.r5.common.BaseResourceWrapper;
import org.fujionclinical.fhir.api.r5.common.ConceptTransform;
import org.fujionclinical.fhir.api.r5.common.PeriodTransform;
import org.fujionclinical.fhir.api.r5.common.ReferenceWrapper;
import org.fujionclinical.fhir.api.r5.patient.PatientTransform;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Period;

import java.util.List;

public class EncounterWrapper extends BaseResourceWrapper<Encounter> implements IEncounter {

    private final List<IConcept> types;

    private final ReferenceWrapper<Patient> patientRef;

    private IPeriod period;

    protected EncounterWrapper(Encounter encounter) {
        super(encounter);
        period = PeriodTransform.instance.wrap(encounter.getPeriod());
        types = ConceptTransform.instance.wrap(encounter.getType());
        patientRef = ReferenceWrapper.wrap(Patient.class, encounter.getSubject());
    }

    @Override
    protected List<Identifier> _getIdentifiers() {
        return getWrapped().getIdentifier();
    }

    @Override
    public IPatient getPatient() {
        return PatientTransform.instance.wrap(patientRef.getWrapped());
    }

    @Override
    public void setPatient(IPatient patient) {
        Patient pat = PatientTransform.instance.unwrap(patient);
        patientRef.setResource(pat);
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
            Period wrapped = PeriodTransform.instance.unwrap(period);
            getWrapped().setPeriod(wrapped);
            this.period = PeriodTransform.instance.wrap(wrapped);
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
