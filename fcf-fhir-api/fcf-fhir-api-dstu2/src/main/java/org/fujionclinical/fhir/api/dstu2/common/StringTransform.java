package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.primitive.StringDt;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class StringTransform implements IWrapperTransform<String, StringDt> {

    public static final StringTransform instance = new StringTransform();

    @Override
    public StringDt _unwrap(String value) {
        StringDt stringDt = newWrapped();
        stringDt.setValue(value);
        return stringDt;
    }

    @Override
    public String _wrap(StringDt value) {
        return value.getValue();
    }

    @Override
    public StringDt newWrapped() {
        return new StringDt();
    }

}
