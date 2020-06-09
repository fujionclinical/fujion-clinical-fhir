package org.fujionclinical.fhir.api.common.core;

import ca.uhn.fhir.model.api.Tag;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.instance.model.api.IBaseCoding;

import java.util.List;

public class TagTransform implements IWrapperTransform<IConceptCode, IBaseCoding> {

    public static final TagTransform instance = new TagTransform();

    @Override
    public IBaseCoding _unwrap(IConceptCode value) {
        return new Tag()
                .setSystem(value.getSystem())
                .setCode(value.getCode())
                .setDisplay(value.getText());
    }

    @Override
    public IConceptCode _wrap(IBaseCoding value) {
        return new BaseConceptCodeWrapper<>(value);
    }

}
