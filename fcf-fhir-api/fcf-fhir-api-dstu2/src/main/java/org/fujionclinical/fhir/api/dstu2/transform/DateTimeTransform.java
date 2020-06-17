package org.fujionclinical.fhir.api.dstu2.transform;

import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

public class DateTimeTransform extends AbstractDatatypeTransform<DateTimeWrapper, DateTimeDt> {

    private static final DateTimeTransform instance = new DateTimeTransform();

    public static DateTimeTransform getInstance() {
        return instance;
    }

    private DateTimeTransform() {
        super(DateTimeWrapper.class, DateTimeDt.class);
    }

    @Override
    public DateTimeDt _fromLogicalModel(DateTimeWrapper value) {
        return new DateTimeDt(value.getLegacyDate());
    }

    @Override
    public DateTimeWrapper _toLogicalModel(DateTimeDt value) {
        return value.isEmpty() ? null : new DateTimeWrapper(value.getValue());
    }

}
