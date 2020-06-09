package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.primitive.StringDt;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class StringTransform implements IWrapperTransform<String, StringDt> {

    public static final StringTransform instance = new StringTransform();

    @Override
    public StringDt _unwrap(String value) {
        return new StringDt(value);
    }

    @Override
    public String _wrap(StringDt value) {
        return value.getValue();
    }

}
