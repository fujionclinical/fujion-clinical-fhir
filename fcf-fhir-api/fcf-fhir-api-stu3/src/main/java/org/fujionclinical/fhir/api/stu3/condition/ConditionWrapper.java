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
package org.fujionclinical.fhir.api.stu3.condition;

import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.stu3.common.BaseResourceWrapper;
import org.fujionclinical.fhir.api.stu3.common.ConceptTransform;
import org.fujionclinical.fhir.api.stu3.common.ReferenceWrapper;
import org.fujionclinical.fhir.api.stu3.patient.PatientTransform;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

public class ConditionWrapper extends BaseResourceWrapper<Condition> implements ICondition {

    private final ReferenceWrapper<Patient> patientRef;

    private final ReferenceWrapper<IDomainResource> asserterRef;

    private IPeriod onset = new IPeriod() {
        @Override
        public Date getStartDate() {
            return getWrapped().hasOnsetDateTimeType() ? getWrapped().getOnsetDateTimeType().getValue() : null;
        }

        @Override
        public void setStartDate(Date startDate) {
            getWrapped().setOnset(new DateTimeType(startDate));
        }

        @Override
        public Date getEndDate() {
            return getWrapped().hasAbatementDateTimeType() ? getWrapped().getAbatementDateTimeType().getValue() : null;
        }

        @Override
        public void setEndDate(Date endDate) {
            getWrapped().setAbatement(new DateTimeType(endDate));
        }

    };

    protected ConditionWrapper(Condition resource) {
        super(resource);
        patientRef = ReferenceWrapper.wrap(Patient.class, resource.getSubject());
        asserterRef = ReferenceWrapper.wrap(IDomainResource.class, resource.getAsserter());
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
        patientRef.setResource(PatientTransform.instance.unwrap(patient));
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
        return getWrapped().getAssertedDate();
    }

    @Override
    public void setRecordedDate(Date recorded) {
        getWrapped().setAssertedDate(recorded);
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
        return ConceptTransform.instance.wrap(getWrapped().getCode());
    }

    @Override
    public void setCondition(IConcept condition) {
        getWrapped().setCode(ConceptTransform.instance.unwrap(condition));
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
        return FhirUtil.convertEnum(getWrapped().getClinicalStatus(), ClinicalStatus.class);
    }

    @Override
    public void setClinicalStatus(ClinicalStatus clinicalStatus) {
        getWrapped().setClinicalStatus(FhirUtil.convertEnum(clinicalStatus, Condition.ConditionClinicalStatus.class));
    }

    @Override
    public VerificationStatus getVerificationStatus() {
        return FhirUtil.convertEnum(getWrapped().getVerificationStatus(), VerificationStatus.class);
    }

    @Override
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        getWrapped().setVerificationStatus(FhirUtil.convertEnum(verificationStatus, Condition.ConditionVerificationStatus.class));
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
