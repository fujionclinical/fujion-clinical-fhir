package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.core.IPostalAddress;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r5.model.Address;

public class PostalAddressTransform implements IWrapperTransform<IPostalAddress, Address> {

    public static final PostalAddressTransform instance = new PostalAddressTransform();

    @Override
    public IPostalAddress _wrap(Address value) {
        return new PostalAddressWrapper(value);
    }

    @Override
    public Address newWrapped() {
        return new Address();
    }

}
