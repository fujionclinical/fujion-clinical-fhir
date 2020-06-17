package org.fujionclinical.fhir.api.dstu2.transform;

import ca.uhn.fhir.model.primitive.DateDt;
import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

import java.util.Date;

public class DateTransform extends AbstractDatatypeTransform<DateTimeWrapper, DateDt> {

    private static final DateTransform instance = new DateTransform();

    public static DateTransform getInstance() {
        return instance;
    }

    private DateTransform() {
        super(DateTimeWrapper.class, DateDt.class);
    }

    @Override
    public DateDt _fromLogicalModel(DateTimeWrapper value) {
        return new DateDt(value.getLegacyDate());
    }

    @Override
    public DateTimeWrapper _toLogicalModel(DateDt value) {
        return new DateTimeWrapper(value.getValue());
    }

    public DateTimeWrapper toLogicalModel(Date value) {
        return value == null ? null : new DateTimeWrapper(value);
    }

}
