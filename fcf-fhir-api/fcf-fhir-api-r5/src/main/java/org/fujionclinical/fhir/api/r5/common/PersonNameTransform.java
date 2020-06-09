package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.person.IPersonName;
import org.hl7.fhir.r5.model.HumanName;

public class PersonNameTransform implements IWrapperTransform<IPersonName, HumanName> {

    public static final PersonNameTransform instance = new PersonNameTransform();

    @Override
    public IPersonName _wrap(HumanName value) {
        return new PersonNameWrapper(value);
    }

    @Override
    public HumanName newWrapped() {
        return new HumanName();
    }

}
