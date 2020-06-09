package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.StringType;

public class StringTransform implements IWrapperTransform<String, StringType> {

    public static final StringTransform instance = new StringTransform();
    
    @Override
    public StringType _unwrap(String value) {
        StringType stringType = newWrapped();
        stringType.setValue(value);
        return stringType;
    }

    @Override
    public String _wrap(StringType value) {
        return value.getValue();
    }

    @Override
    public StringType newWrapped() {
        return new StringType();
    }

}
