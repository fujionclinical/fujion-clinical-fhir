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
package org.fujionclinical.fhir.api.r5.condition;

import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.r5.common.FhirUtilR5;
import org.fujionclinical.fhir.api.r5.transform.*;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.DateTimeType;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.Period;

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
        dest.setRecordedDateElement(DateTimeTransform.getInstance().fromLogicalModel(src.getRecordedDate()));
        dest.setCode(ConceptTransform.getInstance().fromLogicalModel(src.getCondition()));
        dest.setClinicalStatus(FhirUtilR5.convertEnumToCodeableConcept(src.getClinicalStatus(), "http://terminology.hl7.org/CodeSystem/condition-clinical"));
        dest.setVerificationStatus(FhirUtilR5.convertEnumToCodeableConcept(src.getVerificationStatus(), "http://hl7.org/fhir/ValueSet/condition-ver-status"));
        dest.setSeverity(FhirUtilR5.convertEnumToCodeableConcept(src.getSeverity(), "http://hl7.org/fhir/ValueSet/condition-severity"));
        dest.setNote(AnnotationTransform.getInstance().fromLogicalModelAsList(src.getNotes()));
        return dest;
    }

    @Override
    public ICondition _toLogicalModel(Condition src) {
        ICondition dest = super._toLogicalModel(src);
        dest.setOnset(toPeriod(src));
        dest.setPatient(ReferenceTransform.getInstance().toLogicalModel(src.getSubject()));
        dest.setEncounter(ReferenceTransform.getInstance().toLogicalModel(src.getEncounter()));
        dest.setAsserter(ReferenceTransform.getInstance().toLogicalModel(src.getAsserter()));
        dest.setRecordedDate(DateTimeTransform.getInstance().toLogicalModel(src.getRecordedDate()));
        dest.setCondition(ConceptTransform.getInstance().toLogicalModel(src.getCode()));
        dest.setClinicalStatus(FhirUtilR5.convertCodeableConceptToEnum(src.getClinicalStatus(), ICondition.ClinicalStatus.class));
        dest.setVerificationStatus(FhirUtilR5.convertCodeableConceptToEnum(src.getVerificationStatus(), ICondition.VerificationStatus.class));
        dest.setNotes(AnnotationTransform.getInstance().toLogicalModelAsList(src.getNote()));
        return dest;
    }

    private IPeriod toPeriod(Condition src) {
        IBaseDatatype value = src.getOnset();

        if (value instanceof Period) {
            return PeriodTransform.getInstance().toLogicalModel((Period) value);
        } else if (value instanceof DateTimeType) {
            DateTimeType value2 = FhirUtil.castTo(src.getAbatement(), DateTimeType.class);
            Period period = new Period().setStartElement((DateTimeType) value).setEndElement(value2);
            return PeriodTransform.getInstance().toLogicalModel(period);
        }

        return null;
    }

    @Override
    protected List<Identifier> getIdentifiers(Condition condition) {
        return condition.getIdentifier();
    }

}
