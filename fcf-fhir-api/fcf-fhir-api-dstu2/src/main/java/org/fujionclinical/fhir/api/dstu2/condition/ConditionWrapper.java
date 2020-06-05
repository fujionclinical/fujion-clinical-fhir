package org.fujionclinical.fhir.api.dstu2.condition;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.ConditionClinicalStatusCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ConditionVerificationStatusEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IAnnotation;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.core.Period;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.common.core.ResourceWrapper;
import org.fujionclinical.fhir.api.dstu2.common.ConceptWrapper;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtilDstu2;
import org.fujionclinical.fhir.api.dstu2.patient.PatientWrapper;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

public class ConditionWrapper extends ResourceWrapper<Condition> implements ICondition {

    private final ResourceReferenceDt patientRef;

    private final ResourceReferenceDt asserterRef;

    private PatientWrapper patient;

    public static ConditionWrapper wrap(Condition condition) {
        return condition == null ? null : new ConditionWrapper(condition);
    }

    public static Condition unwrap(ICondition condition) {
        if (condition == null) {
            return null;
        }

        if (condition instanceof ConditionWrapper) {
            return ((ConditionWrapper) condition).getWrapped();
        }

        ConditionWrapper cond = wrap(new Condition());
        BeanUtils.copyProperties(condition, cond);
        return cond.getWrapped();
    }

    private ConditionWrapper(Condition resource) {
        super(resource);
        patientRef = resource.getPatient();
        asserterRef = resource.getAsserter();
        initPatientWrapper();
    }

    private void initPatientWrapper() {
        patient = PatientWrapper.wrap(FhirUtilDstu2.getFhirService().getResource(patientRef, Patient.class));
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
    public IPeriod getOnset() {
        IDatatype onset = getWrapped().getOnset();
        IDatatype abatement = getWrapped().getOnset();
        return new Period(toDate(onset), toDate(abatement));
    }

    @Override
    public void setOnset(IPeriod period) {

    }

    private Date toDate(IDatatype value) {
        return value instanceof DateDt ? ((DateDt) value).getValue() : null;
    }

    @Override
    public Date getRecorded() {
        return getWrapped().getDateRecorded();
    }

    @Override
    public void setRecorded(Date recorded) {
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
        return ConceptWrapper.wrap(getWrapped().getCode());
    }

    @Override
    public void setCondition(IConcept condition) {
        getWrapped().setCode(ConceptWrapper.unwrap(condition));
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
