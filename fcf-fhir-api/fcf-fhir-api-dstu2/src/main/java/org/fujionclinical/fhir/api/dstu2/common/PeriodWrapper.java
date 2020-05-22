package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import org.fujionclinical.api.model.IPeriod;
import org.springframework.security.core.parameters.P;

import java.util.Date;

public class PeriodWrapper implements IPeriod {

    public static PeriodWrapper create(PeriodDt period) {
        return period == null ? null : new PeriodWrapper(period);
    }

    public static final PeriodDt unwrap(IPeriod period) {
        return new PeriodDt()
                .setStartWithSecondsPrecision(period.getStartDate())
                .setEndWithSecondsPrecision(period.getEndDate());
    }

    private final PeriodDt period;

    private PeriodWrapper(PeriodDt period) {
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
