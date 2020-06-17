package org.fujionclinical.fhir.api.dstu2.transform;

import ca.uhn.fhir.model.api.IPrimitiveDatatype;
import ca.uhn.fhir.model.primitive.*;
import org.fujion.common.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;

import java.math.BigDecimal;
import java.util.Date;

public class PrimitiveTransform extends AbstractDatatypeTransform<Object, IPrimitiveDatatype> {

    private static final PrimitiveTransform instance = new PrimitiveTransform();

    public static PrimitiveTransform getInstance() {
        return instance;
    }

    private PrimitiveTransform() {
        super(Object.class, IPrimitiveDatatype.class);
    }

    @Override
    public IPrimitiveDatatype _fromLogicalModel(Object src) {
        if (src instanceof String) {
            return new StringDt((String) src);
        } else if (src instanceof Boolean) {
            return new BooleanDt((Boolean) src);
        } else if (src instanceof Integer) {
            return new IntegerDt((Integer) src);
        } else if (src instanceof Double) {
            return new DecimalDt(BigDecimal.valueOf((Double) src));
        } else if (src instanceof DateTimeWrapper) {
            DateTimeWrapper dtw = (DateTimeWrapper) src;
            return dtw.hasTime() ? new DateTimeDt(dtw.getLegacyDate()) : new DateDt(dtw.getLegacyDate());
        } else {
            return notSupported();
        }
    }

    @Override
    public Object _toLogicalModel(IPrimitiveDatatype src) {
        Object value = src.getValue();
        return value instanceof Date ? new DateTimeWrapper((Date) value) : value;
    }

}
