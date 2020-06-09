package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import org.fujionclinical.api.model.core.IPostalAddress;
import org.fujionclinical.api.model.core.IWrapperTransform;

public class PostalAddressTransform implements IWrapperTransform<IPostalAddress, AddressDt> {

    public static final PostalAddressTransform instance = new PostalAddressTransform();

    @Override
    public AddressDt _unwrap(IPostalAddress value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPostalAddress _wrap(AddressDt value) {
        return new PostalAddressWrapper(value);
    }

}
