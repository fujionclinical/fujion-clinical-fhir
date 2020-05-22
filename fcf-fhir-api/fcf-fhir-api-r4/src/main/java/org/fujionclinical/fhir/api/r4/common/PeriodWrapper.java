package org.fujionclinical.fhir.api.r4.common;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import org.fujionclinical.api.model.IPeriod;
import org.hl7.fhir.r4.model.Period;

import java.util.Date;

public class PeriodWrapper implements IPeriod {

    public static PeriodWrapper create(Period period) {
        return period == null ? null : new PeriodWrapper(period);
    }

    public static final Period unwrap(IPeriod period) {
        return new Period()
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
    public PeriodWrapper setStartDate(Date date) {
        period.setStart(date, TemporalPrecisionEnum.SECOND);
        return this;
    }

    @Override
    public Date getEndDate() {
        return period.getStart();
    }

    @Override
    public PeriodWrapper setEndDate(Date date) {
        period.setEnd(date, TemporalPrecisionEnum.SECOND);
        return this;
    }
}
