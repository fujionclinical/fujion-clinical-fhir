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
package org.fujionclinical.fhir.api.r4.condition;

import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.r4.common.FhirUtilR4;
import org.fujionclinical.fhir.api.r4.transform.*;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;

import java.util.List;

public class ConditionTransform extends BaseResourceTransform<ICondition, Condition> {

    private static final ConditionTransform instance = new ConditionTransform();

    public static ConditionTransform getInstance() {
        return instance;
    }

    private ConditionTransform() {
        super(ICondition.class, Condition.class);
    }

    @Override
    protected ICondition newLogical() {
        return new org.fujionclinical.api.model.condition.Condition();
    }

    @Override
    protected Condition newNative() {
        return new Condition();
    }

    @Override
    public Condition _fromLogicalModel(ICondition src) {
        Condition dest = super._fromLogicalModel(src);
        dest.setOnset(PeriodTransform.getInstance().fromLogicalModel(src.getOnset()));
        dest.setSubject(ReferenceTransform.getInstance().fromLogicalModel(src.getPatient()));
        dest.setEncounter(ReferenceTransform.getInstance().fromLogicalModel(src.getEncounter()));
        dest.setAsserter(ReferenceTransform.getInstance().fromLogicalModel(src.getAsserter()));
        dest.setRecordedDate(DateTransform.getInstance().fromLogicalModel(src.getRecordedDate()));
        dest.setCode(ConceptTransform.getInstance().fromLogicalModel(src.getCondition()));
        dest.setClinicalStatus(FhirUtilR4.convertEnumToCodeableConcept(src.getClinicalStatus(), "http://terminology.hl7.org/CodeSystem/condition-clinical"));
        dest.setVerificationStatus(FhirUtilR4.convertEnumToCodeableConcept(src.getVerificationStatus(), "http://hl7.org/fhir/ValueSet/condition-ver-status"));
        dest.setSeverity(FhirUtilR4.convertEnumToCodeableConcept(src.getSeverity(), "http://hl7.org/fhir/ValueSet/condition-severity"));
        dest.setNote(AnnotationTransform.getInstance().fromLogicalModel(src.getAnnotations()));
        return dest;
    }

    @Override
    public ICondition _toLogicalModel(Condition src) {
        ICondition dest = super._toLogicalModel(src);
        dest.setOnset(PeriodTransform.getInstance().toLogicalModel(FhirUtil.castTo(src.getOnset(), Period.class)));
        dest.setPatient(ReferenceTransform.getInstance().toLogicalModel(src.getSubject()));
        dest.setEncounter(ReferenceTransform.getInstance().toLogicalModel(src.getEncounter()));
        dest.setAsserter(ReferenceTransform.getInstance().toLogicalModel(src.getAsserter()));
        dest.setRecordedDate(DateTransform.getInstance().toLogicalModel(src.getRecordedDate()));
        dest.setCondition(ConceptTransform.getInstance().toLogicalModel(src.getCode()));
        dest.setClinicalStatus(FhirUtilR4.convertCodeableConceptToEnum(src.getClinicalStatus(), ICondition.ClinicalStatus.class));
        dest.setVerificationStatus(FhirUtilR4.convertCodeableConceptToEnum(src.getVerificationStatus(), ICondition.VerificationStatus.class));
        dest.setAnnotations(AnnotationTransform.getInstance().toLogicalModel(src.getNote()));
        return dest;
    }

    @Override
    protected List<Identifier> getIdentifiers(Condition condition) {
        return condition.getIdentifier();
    }

}
