package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class PeriodTransform implements IWrapperTransform<IPeriod, PeriodDt> {

    public static final PeriodTransform instance = new PeriodTransform();

    @Override
    public PeriodDt _unwrap(IPeriod value) {
        return new PeriodDt()
                .setStartWithSecondsPrecision(value.getStartDate())
                .setEndWithSecondsPrecision(value.getEndDate());
    }

    @Override
    public IPeriod _wrap(PeriodDt value) {
        return new PeriodWrapper(value);
    }

}
