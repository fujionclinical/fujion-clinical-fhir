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

import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.fhir.api.r5.transform.BaseResourceTransform;
import org.fujionclinical.fhir.api.r5.transform.ConceptTransform;
import org.fujionclinical.fhir.api.r5.transform.PeriodTransform;
import org.fujionclinical.fhir.api.r5.transform.ReferenceTransform;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.Identifier;

import java.util.List;

public class EncounterTransform extends BaseResourceTransform<IEncounter, Encounter> {

    private static final EncounterTransform instance = new EncounterTransform();

    public static EncounterTransform getInstance() {
        return instance;
    }

    private EncounterTransform() {
        super(IEncounter.class, Encounter.class);
    }

    @Override
    protected IEncounter newLogical() {
        return new org.fujionclinical.api.model.encounter.Encounter();
    }

    @Override
    protected Encounter newNative() {
        return new Encounter();
    }

    @Override
    protected List<Identifier> getIdentifiers(Encounter encounter) {
        return encounter.getIdentifier();
    }

    @Override
    public Encounter _fromLogicalModel(IEncounter src) {
        Encounter dest = super._fromLogicalModel(src);
        dest.setPeriod(PeriodTransform.getInstance().fromLogicalModel(src.getPeriod()));
        dest.setType((ConceptTransform.getInstance().fromLogicalModel(src.getTypes())));
        dest.setSubject(ReferenceTransform.getInstance().fromLogicalModel(src.getPatient()));
        dest.setStatus(CoreUtil.enumToEnum(src.getStatus(), Encounter.EncounterStatus.class));
        return dest;
    }

    @Override
    public IEncounter _toLogicalModel(Encounter src) {
        IEncounter dest = super._toLogicalModel(src);
        dest.setPeriod(PeriodTransform.getInstance().toLogicalModel(src.getPeriod()));
        dest.setTypes((ConceptTransform.getInstance().toLogicalModel(src.getType())));
        dest.setPatient(ReferenceTransform.getInstance().toLogicalModel(src.getSubject()));
        dest.setStatus(CoreUtil.enumToEnum(src.getStatus(), IEncounter.EncounterStatus.class));
        return dest;
    }

}
