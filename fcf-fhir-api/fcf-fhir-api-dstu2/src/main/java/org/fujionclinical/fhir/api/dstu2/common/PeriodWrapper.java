package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IWrapper;

import java.util.Date;

public class PeriodWrapper implements IPeriod, IWrapper<PeriodDt> {

    private final PeriodDt period;

    public static PeriodWrapper wrap(PeriodDt period) {
        return period == null ? null : new PeriodWrapper(period);
    }

    public static final PeriodDt unwrap(IPeriod period) {
        return period == null ? null : new PeriodDt()
                .setStartWithSecondsPrecision(period.getStartDate())
                .setEndWithSecondsPrecision(period.getEndDate());
    }

    private PeriodWrapper(PeriodDt period) {
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
    public PeriodDt getWrapped() {
        return period;
    }

}
