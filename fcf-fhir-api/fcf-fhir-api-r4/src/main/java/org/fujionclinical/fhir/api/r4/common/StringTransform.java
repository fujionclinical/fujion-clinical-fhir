package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r4.model.StringType;

public class StringTransform implements IWrapperTransform<String, StringType> {

    public static final StringTransform instance = new StringTransform();
    
    @Override
    public StringType _unwrap(String value) {
        return new StringType(value);
    }

    @Override
    public String _wrap(StringType value) {
        return value.getValue();
    }

}
