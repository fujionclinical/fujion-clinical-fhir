package org.fujionclinical.fhir.api.r4.common;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r4.model.Period;

import java.util.Date;

public class PeriodWrapper implements IPeriod, IWrapper<Period> {

    public static PeriodWrapper wrap(Period period) {
        return period == null ? null : new PeriodWrapper(period);
    }

    public static final Period unwrap(IPeriod period) {
        return period == null ? null : new Period()
                .setStart(period.getStartDate())
                .setEnd(period.getEndDate());
    }

    private final Period period;

    private PeriodWrapper(Period period) {
        this.period = period;
    }

    @Override
    public Date getStartDate() {
        return period.getStart();
    }

    @Override
    public void setStartDate(Date date) {
        period.setStart(date, TemporalPrecisionEnum.SECOND);
    }

    @Override
    public Date getEndDate() {
        return period.getStart();
    }

    @Override
    public void setEndDate(Date date) {
        period.setEnd(date, TemporalPrecisionEnum.SECOND);
    }

    @Override
    public Period getWrapped() {
        return period;
    }
}
