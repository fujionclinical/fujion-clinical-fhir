package org.fujionclinical.fhir.api.dstu2.observation;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.*;
import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.api.model.core.*;
import org.fujionclinical.api.model.observation.IObservationType;
import org.fujionclinical.fhir.api.dstu2.common.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ObservationComponentWrapper extends AbstractWrapper<Observation.Component> implements IObservationType {

    private IConcept code;

    private List<IReferenceRange<Double>> referenceRanges;

    protected ObservationComponentWrapper(Observation.Component wrapped) {
        super(wrapped);
        this.code = ConceptTransform.getInstance().wrap(wrapped.getCode());
        this.referenceRanges = new WrappedList<IReferenceRange<Double>, Observation.ReferenceRange>(wrapped.getReferenceRange(),
                ReferenceRangeTransform.getInstance());
    }

    @Override
    public IConcept getCode() {
        return code;
    }

    @Override
    public void setCode(IConcept value) {
        this.code = value;
        getWrapped().setCode(ConceptTransform.getInstance().unwrap(value));
    }

    @Override
    public List<IReferenceRange<Double>> getReferenceRanges() {
        return referenceRanges;
    }

    @Override
    public DataAbsentReason getDataAbsentReason() {
        return null;
    }

    @Override
    public void setDataAbsentReason(DataAbsentReason value) {

    }

    @Override
    public boolean hasValue() {
        return !getWrapped().getValue().isEmpty();
    }

    @Override
    public String getValueAsString() {
        return StringTransform.getInstance().wrap(getValue(StringDt.class));
    }

    @Override
    public void setValueAsString(String value) {
        getWrapped().setValue(StringTransform.getInstance().unwrap(value));
    }

    @Override
    public IConcept getValueAsConcept() {
        CodeableConceptDt value = getValue(CodeableConceptDt.class);
        return ConceptTransform.getInstance().wrap(value);
    }

    @Override
    public void setValueAsConcept(IConcept value) {
        getWrapped().setValue(ConceptTransform.getInstance().unwrap(value));
    }

    @Override
    public Boolean getValueAsBoolean() {
        BooleanDt value = getValue(BooleanDt.class);
        return value == null ? null : value.getValue();
    }

    @Override
    public void setValueAsBoolean(Boolean value) {
        getWrapped().setValue(new BooleanDt(value));
    }

    @Override
    public Integer getValueAsInteger() {
        IntegerDt value = getValue(IntegerDt.class);
        return value == null ? null : value.getValue();
    }

    @Override
    public void setValueAsInteger(Integer value) {
        getWrapped().setValue(new IntegerDt(value));
    }

    @Override
    public DateTimeWrapper getValueAsDateTime() {
        DateTimeDt value = getValue(DateTimeDt.class);
        return FhirUtilDstu2.convertDate(value);
    }

    @Override
    public void setValueAsDateTime(DateTimeWrapper value) {
        getWrapped().setValue(FhirUtilDstu2.convertDateToType(value));
    }

    @Override
    public LocalTime getValueAsTime() {
        TimeDt value = getValue(TimeDt.class);
        return value == null ? null : LocalTime.parse(value.getValue());
    }

    @Override
    public void setValueAsTime(LocalTime value) {
        getWrapped().setValue(value == null ? null : new TimeDt(DateTimeFormatter.ISO_TIME.format(value)));
    }

    @Override
    public IPeriod getValueAsPeriod() {
        PeriodDt value = getValue(PeriodDt.class);
        return PeriodTransform.getInstance().wrap(value);
    }

    @Override
    public void setValueAsPeriod(IPeriod value) {
        getWrapped().setValue(PeriodTransform.getInstance().unwrap(value));
    }

    @Override
    public IRange getValueAsRange() {
        return null;
    }

    @Override
    public void setValueAsRange(IRange value) {

    }

    @Override
    public IQuantity<Double> getValueAsQuantity() {
        SimpleQuantityDt value = getValue(SimpleQuantityDt.class);
        return value == null ? null : QuantityTransform.getInstance().wrap(value);
    }

    @Override
    public void setValueAsQuantity(IQuantity<Double> value) {
        getWrapped().setValue(value == null ? null : QuantityTransform.getInstance().unwrap(value));
    }

    @Override
    public IRatio<Double> getValueAsRatio() {
        RatioDt value = getValue(RatioDt.class);
        return null;
    }

    @Override
    public void setValueAsRatio(IRatio<Double> value) {

    }

    private <T extends IDatatype, V> T getValue(Class<T> clazz) {
        if (hasValue() && clazz.isInstance(getWrapped().getValue())) {
            return (T) getWrapped().getValue();
        }

        return null;
    }

}
