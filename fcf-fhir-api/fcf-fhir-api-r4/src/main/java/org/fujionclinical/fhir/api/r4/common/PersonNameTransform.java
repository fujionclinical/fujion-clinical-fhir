package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.person.IPersonName;
import org.hl7.fhir.r4.model.HumanName;

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
