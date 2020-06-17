package org.fujionclinical.fhir.api.r4.transform;

import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.transform.AbstractModelTransform;

import java.util.Date;

public class DateTransform extends AbstractModelTransform<DateTimeWrapper, Date> {

    private static final DateTransform instance = new DateTransform();

    public static DateTransform getInstance() {
        return instance;
    }

    private DateTransform() {
        super(DateTimeWrapper.class, Date.class);
    }

    @Override
    public Date _fromLogicalModel(DateTimeWrapper value) {
        return value.getLegacyDate();
    }

    @Override
    public DateTimeWrapper _toLogicalModel(Date value) {
        return new DateTimeWrapper(value);
    }

}
