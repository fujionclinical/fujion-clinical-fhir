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
package org.fujionclinical.fhir.api.dstu2.observation;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.*;
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
        RangeDt value = getValue(RangeDt.class);
        return RangeTransform.getInstance().wrap(value);
    }

    @Override
    public void setValueAsRange(IRange value) {
        getWrapped().setValue(RangeTransform.getInstance().unwrap(value));
    }

    @Override
    public IQuantity<Double> getValueAsQuantity() {
        QuantityDt value = getValue(QuantityDt.class);
        return value == null ? null : QuantityTransform.getInstance().wrap(value);
    }

    @Override
    public void setValueAsQuantity(IQuantity<Double> value) {
        getWrapped().setValue(value == null ? null : QuantityTransform.getInstance().unwrap(value));
    }

    @Override
    public IRatio<Double> getValueAsRatio() {
        RatioDt value = getValue(RatioDt.class);
        return RatioTransform.getInstance().wrap(value);
    }

    @Override
    public void setValueAsRatio(IRatio<Double> value) {
        getWrapped().setValue(RatioTransform.getInstance().unwrap(value));
    }

    private <T extends IDatatype, V> T getValue(Class<T> clazz) {
        if (hasValue() && clazz.isInstance(getWrapped().getValue())) {
            return (T) getWrapped().getValue();
        }

        return null;
    }

}
