package org.fujionclinical.fhir.api.r5.transform;

import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r5.model.*;

import java.math.BigDecimal;
import java.util.Date;

public class PrimitiveTransform extends AbstractDatatypeTransform<Object, PrimitiveType> {

    private static final PrimitiveTransform instance = new PrimitiveTransform();

    public static PrimitiveTransform getInstance() {
        return instance;
    }

    private PrimitiveTransform() {
        super(Object.class, PrimitiveType.class);
    }

    @Override
    public PrimitiveType _fromLogicalModel(Object src) {
        if (src instanceof String) {
            return new StringType((String) src);
        } else if (src instanceof Boolean) {
            return new BooleanType((Boolean) src);
        } else if (src instanceof Integer) {
            return new IntegerType((Integer) src);
        } else if (src instanceof Double) {
            return new DecimalType(BigDecimal.valueOf((Double) src));
        } else if (src instanceof DateTimeWrapper) {
            DateTimeWrapper dtw = (DateTimeWrapper) src;
            return dtw.hasTime() ? new DateTimeType(dtw.getLegacyDate()) : new DateType(dtw.getLegacyDate());
        } else {
            return notSupported();
        }
    }

    @Override
    public Object _toLogicalModel(PrimitiveType src) {
        Object value = src.getValue();
        return value instanceof Date ? new DateTimeWrapper((Date) value) : value;
    }

}
