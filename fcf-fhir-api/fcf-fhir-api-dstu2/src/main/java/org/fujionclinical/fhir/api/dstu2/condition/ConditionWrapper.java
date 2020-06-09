package org.fujionclinical.fhir.api.dstu2.condition;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.ConditionClinicalStatusCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ConditionVerificationStatusEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.dstu2.common.BaseResourceWrapper;
import org.fujionclinical.fhir.api.dstu2.common.ConceptTransform;
import org.fujionclinical.fhir.api.dstu2.common.ReferenceWrapper;
import org.fujionclinical.fhir.api.dstu2.patient.PatientTransform;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

public class ConditionWrapper extends BaseResourceWrapper<Condition> implements ICondition {

    private final ReferenceWrapper<Patient> patientRef;

    private final ReferenceWrapper<Patient> asserterRef;

    private IPeriod onset = new IPeriod() {
        @Override
        public Date getStartDate() {
            return toDate(getWrapped().getOnset());
        }

        @Override
        public void setStartDate(Date startDate) {
            getWrapped().setOnset(new DateDt(startDate));
        }

        @Override
        public Date getEndDate() {
            return toDate(getWrapped().getAbatement());
        }

        @Override
        public void setEndDate(Date endDate) {
            getWrapped().setAbatement(new DateDt(endDate));
        }

        private Date toDate(IDatatype value) {
            return value instanceof DateDt ? ((DateDt) value).getValue() : null;
        }

    };

    private IPatient patient;

    protected ConditionWrapper(Condition resource) {
        super(resource);
        patientRef = ReferenceWrapper.wrap(Patient.class, resource.getPatient());
        asserterRef = ReferenceWrapper.wrap(IDomainResource.class, resource.getAsserter());
    }

    @Override
    protected List<IdentifierDt> _getIdentifiers() {
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
        return getWrapped().getDateRecorded();
    }

    @Override
    public void setRecordedDate(Date recorded) {
        getWrapped().setDateRecorded(recorded, TemporalPrecisionEnum.DAY);
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
        return FhirUtil.convertEnum(getWrapped().getClinicalStatusElement().getValueAsEnum(), ClinicalStatus.class);
    }

    @Override
    public void setClinicalStatus(ClinicalStatus clinicalStatus) {
        getWrapped().setClinicalStatus(FhirUtil.convertEnum(clinicalStatus, ConditionClinicalStatusCodesEnum.class));
    }

    @Override
    public VerificationStatus getVerificationStatus() {
        return FhirUtil.convertEnum(getWrapped().getVerificationStatusElement().getValueAsEnum(), VerificationStatus.class);
    }

    @Override
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        getWrapped().setVerificationStatus(FhirUtil.convertEnum(verificationStatus, ConditionVerificationStatusEnum.class));
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
