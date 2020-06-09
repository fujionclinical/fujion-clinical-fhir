package org.fujionclinical.fhir.api.r5.condition;

import org.fujion.common.CollectionUtil;
import org.fujionclinical.api.model.condition.ICondition;
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
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.hl7.fhir.r5.model.*;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

import static org.fujionclinical.fhir.api.common.core.Constants.CONDITION_CLINICAL_STATUS_SYSTEM;
import static org.fujionclinical.fhir.api.common.core.Constants.CONDITION_VERIFICATION_STATUS_SYSTEM;

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
