package org.fujionclinical.fhir.api.r4.transform;

import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r4.model.DateTimeType;

public class DateTimeTransform extends AbstractDatatypeTransform<DateTimeWrapper, DateTimeType> {

    private static final DateTimeTransform instance = new DateTimeTransform();

    public static DateTimeTransform getInstance() {
        return instance;
    }

    private DateTimeTransform() {
        super(DateTimeWrapper.class, DateTimeType.class);
    }

    @Override
    public DateTimeType _fromLogicalModel(DateTimeWrapper value) {
        return new DateTimeType(value.getLegacyDate());
    }

    @Override
    public DateTimeWrapper _toLogicalModel(DateTimeType value) {
        return new DateTimeWrapper(value.getValue());
    }

}
