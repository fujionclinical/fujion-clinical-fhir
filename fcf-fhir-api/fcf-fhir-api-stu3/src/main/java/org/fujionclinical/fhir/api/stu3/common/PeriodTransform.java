package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Period;

public class PeriodTransform implements IWrapperTransform<IPeriod, Period> {

    public static final PeriodTransform instance = new PeriodTransform();

    @Override
    public Period _unwrap(IPeriod value) {
        return new Period()
                .setStart(value.getStartDate())
                .setEnd(value.getEndDate());
    }

    @Override
    public IPeriod _wrap(Period value) {
        return new PeriodWrapper(value);
    }

}
