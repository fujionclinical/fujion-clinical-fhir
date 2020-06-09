package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.person.IPersonName;

public class PersonNameTransform implements IWrapperTransform<IPersonName, HumanNameDt> {

    public static final PersonNameTransform instance = new PersonNameTransform();

    @Override
    public HumanNameDt _unwrap(IPersonName value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPersonName _wrap(HumanNameDt value) {
        return new PersonNameWrapper(value);
    }

}
