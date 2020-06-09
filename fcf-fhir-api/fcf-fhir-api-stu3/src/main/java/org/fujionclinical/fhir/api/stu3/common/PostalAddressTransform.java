package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IPostalAddress;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Address;

public class PostalAddressTransform implements IWrapperTransform<IPostalAddress, Address> {

    public static final PostalAddressTransform instance = new PostalAddressTransform();

    @Override
    public Address _unwrap(IPostalAddress value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPostalAddress _wrap(Address value) {
        return new PostalAddressWrapper(value);
    }

}
