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

import org.fujion.common.CollectionUtil;
import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.DateTimeWrapper;
import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.r5.common.BaseResourceWrapper;
import org.fujionclinical.fhir.api.r5.common.ConceptTransform;
import org.fujionclinical.fhir.api.r5.common.FhirUtilR5;
import org.fujionclinical.fhir.api.r5.common.ReferenceWrapper;
import org.fujionclinical.fhir.api.r5.patient.PatientTransform;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.Identifier;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

import static org.fujionclinical.fhir.api.common.core.Constants.CONDITION_CLINICAL_STATUS_SYSTEM;
import static org.fujionclinical.fhir.api.common.core.Constants.CONDITION_VERIFICATION_STATUS_SYSTEM;

public class ConditionWrapper extends BaseResourceWrapper<Condition> implements ICondition {

    private final ReferenceWrapper<IPatient> patientRef;

    private final ReferenceWrapper<IPerson> asserterRef;

    private IPeriod onset = new IPeriod() {
        @Override
        public DateTimeWrapper getStartDate() {
            return FhirUtilR5.convertDate(getWrapped().getOnset());
        }

        @Override
        public void setStartDate(DateTimeWrapper startDate) {
            getWrapped().setOnset(FhirUtilR5.convertDateToType(startDate));
        }

        @Override
        public DateTimeWrapper getEndDate() {
            return FhirUtilR5.convertDate(getWrapped().getAbatement());
        }

        @Override
        public void setEndDate(DateTimeWrapper endDate) {
            getWrapped().setAbatement(FhirUtilR5.convertDateToType(endDate));
        }

    };

    protected ConditionWrapper(Condition resource) {
        super(resource);
        patientRef = ReferenceWrapper.wrap(resource.getSubject());
        asserterRef = ReferenceWrapper.wrap(resource.getAsserter());
    }

    @Override
    protected List<Identifier> _getIdentifiers() {
        return getWrapped().getIdentifier();
    }

    @Override
    public IPatient getPatient() {
        return patientRef.getWrapped();
    }

    @Override
    public void setPatient(IPatient patient) {
        patientRef.setResource(PatientTransform.getInstance().unwrap(patient));
    }

    @Override
    public IPeriod getOnset() {
        return onset;
    }

    @Override
    public void setOnset(IPeriod period) {
        BeanUtils.copyProperties(period, this.onset);
    }

    @Override
    public Date getRecordedDate() {
        return getWrapped().getRecordedDate();
    }

    @Override
    public void setRecordedDate(Date recorded) {
        getWrapped().setRecordedDate(recorded);
    }

    @Override
    public IPerson getRecorder() {
        return null;
    }

    @Override
    public void setRecorder(IPerson recorder) {

    }

    @Override
    public IPerson getAsserter() {
        return null;
    }

    @Override
    public void setAsserter(IPerson asserter) {

    }

    @Override
    public IConcept getCondition() {
        return ConceptTransform.getInstance().wrap(getWrapped().getCode());
    }

    @Override
    public void setCondition(IConcept condition) {
        getWrapped().setCode(ConceptTransform.getInstance().unwrap(condition));
    }

    @Override
    public IEncounter getEncounter() {
        return null;
    }

    @Override
    public void setEncounter(IEncounter encounter) {

    }

    @Override
    public ClinicalStatus getClinicalStatus() {
        Coding code = CollectionUtil.getFirst(getWrapped().getClinicalStatus().getCoding());
        return code == null ? null : FhirUtil.convertEnum(code.getCode(), ClinicalStatus.class);
    }

    @Override
    public void setClinicalStatus(ClinicalStatus clinicalStatus) {
        CodeableConcept code = FhirUtilR5.createCodeableConcept(CONDITION_CLINICAL_STATUS_SYSTEM, clinicalStatus.name().toLowerCase(), clinicalStatus.toString());
        getWrapped().setClinicalStatus(code);
    }

    @Override
    public VerificationStatus getVerificationStatus() {
        Coding code = CollectionUtil.getFirst(getWrapped().getVerificationStatus().getCoding());
        return code == null ? null : FhirUtil.convertEnum(code.getCode(), VerificationStatus.class);
    }

    @Override
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        CodeableConcept code = FhirUtilR5.createCodeableConcept(CONDITION_VERIFICATION_STATUS_SYSTEM, verificationStatus.name().toLowerCase(), verificationStatus.toString());
        getWrapped().setVerificationStatus(code);
    }

    @Override
    public Severity getSeverity() {
        return FhirUtil.convertEnum(getWrapped().getSeverity().getText(), Severity.class);
    }

    @Override
    public void setSeverity(Severity severity) {

    }

    @Override
    public List<IAnnotation> getAnnotations() {
        return null;
    }

}
