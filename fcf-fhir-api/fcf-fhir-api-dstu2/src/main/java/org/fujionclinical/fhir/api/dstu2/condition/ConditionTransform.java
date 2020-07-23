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
package org.fujionclinical.fhir.api.dstu2.condition;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.valueset.ConditionClinicalStatusCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ConditionVerificationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.apache.commons.lang3.StringUtils;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.impl.Annotation;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtilDstu2;
import org.fujionclinical.fhir.api.dstu2.transform.*;
import org.hl7.fhir.instance.model.api.IBaseDatatype;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        dest.setPatient(ReferenceTransform.getInstance().fromLogicalModel(src.getPatient()));
        dest.setEncounter(ReferenceTransform.getInstance().fromLogicalModel(src.getEncounter()));
        dest.setAsserter(ReferenceTransform.getInstance().fromLogicalModel(src.getAsserter()));
        dest.setDateRecorded(DateTransform.getInstance().fromLogicalModel(src.getRecordedDate()));
        dest.setCode(ConceptTransform.getInstance().fromLogicalModel(src.getCondition()));
        dest.setClinicalStatus(CoreUtil.enumToEnum(src.getClinicalStatus(), ConditionClinicalStatusCodesEnum.class));
        dest.setVerificationStatus(CoreUtil.enumToEnum(src.getVerificationStatus(), ConditionVerificationStatusEnum.class));
        dest.setSeverity(FhirUtilDstu2.convertEnumToCodeableConcept(src.getSeverity(), "http://hl7.org/fhir/ValueSet/condition-severity"));
        dest.setNotes(src.getAnnotations().stream()
                .map(annotation -> annotation.getText())
                .collect(Collectors.joining("\n")));
        return dest;
    }

    @Override
    public ICondition _toLogicalModel(Condition src) {
        ICondition dest = super._toLogicalModel(src);
        dest.setOnset(toPeriod(src));
        dest.setPatient(ReferenceTransform.getInstance().toLogicalModel(src.getPatient()));
        dest.setEncounter(ReferenceTransform.getInstance().toLogicalModel(src.getEncounter()));
        dest.setAsserter(ReferenceTransform.getInstance().toLogicalModel(src.getAsserter()));
        dest.setRecordedDate(DateTransform.getInstance().toLogicalModel(src.getDateRecorded()));
        dest.setCondition(ConceptTransform.getInstance().toLogicalModel(src.getCode()));
        dest.setClinicalStatus(CoreUtil.stringToEnum(src.getClinicalStatus(), ICondition.ClinicalStatus.class));
        dest.setVerificationStatus(CoreUtil.stringToEnum(src.getVerificationStatus(), ICondition.VerificationStatus.class));

        if (!src.getNotesElement().isEmpty()) {
            Arrays.stream(src.getNotes().split("\\n"))
                    .map(note -> StringUtils.trimToNull(note))
                    .filter(Objects::nonNull)
                    .map(note -> new Annotation(note))
                    .collect(Collectors.toList());
        }

        return dest;
    }

    private IPeriod toPeriod(Condition src) {
        IBaseDatatype value = src.getOnset();

        if (value instanceof PeriodDt) {
            return PeriodTransform.getInstance().toLogicalModel((PeriodDt) value);
        } else if (value instanceof DateTimeDt) {
            DateTimeDt value2 = FhirUtil.castTo(src.getAbatement(), DateTimeDt.class);
            PeriodDt periodDt = new PeriodDt().setStart((DateTimeDt) value).setEnd(value2);
            return PeriodTransform.getInstance().toLogicalModel(periodDt);
        }

        return null;
    }

    @Override
    protected List<IdentifierDt> getIdentifiers(Condition condition) {
        return condition.getIdentifier();
    }

}
