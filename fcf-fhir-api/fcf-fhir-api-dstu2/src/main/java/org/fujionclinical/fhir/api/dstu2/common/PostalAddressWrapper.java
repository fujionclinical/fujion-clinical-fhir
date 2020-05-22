package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.api.BasePrimitive;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IPostalAddress;
import org.fujionclinical.api.model.IWrapper;

import java.util.List;
import java.util.stream.Collectors;

public class PostalAddressWrapper implements IPostalAddress, IWrapper<AddressDt> {

    public static PostalAddressWrapper create(AddressDt address) {
        return address == null ? null : new PostalAddressWrapper(address);
    }

    public static List<IPostalAddress> wrap(List<AddressDt> addresses) {
        return addresses.stream().map(address -> PostalAddressWrapper.create(address)).collect(Collectors.toList());
    }

    public static List<AddressDt> unwrap(List<IWrapper<AddressDt>> addresses) {
        return addresses.stream().map(address -> address.getWrapped()).collect(Collectors.toList());
    }

    private final AddressDt address;

    private final PeriodWrapper period;

    private PostalAddressWrapper(AddressDt address) {
        this.address = address;
        this.period = PeriodWrapper.create(address.getPeriod());
    }

    @Override
    public PostalAddressUse getUse() {
        AddressUseEnum use = address.getUseElement().isEmpty() ? null : address.getUseElement().getValueAsEnum();
        return FhirUtil.convertEnum(use, PostalAddressUse.class);
    }

    @Override
    public IPostalAddress setUse(PostalAddressUse use) {
        address.setUse(FhirUtil.convertEnum(use, AddressUseEnum.class));
        return this;
    }

    @Override
    public List<String> getLines() {
        return address.getLine().stream()
                .map(BasePrimitive::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public String getCity() {
        return address.getCity();
    }

    @Override
    public IPostalAddress setCity(String city) {
        address.setCity(city);
        return this;
    }

    @Override
    public String getDistrict() {
        return address.getDistrict();
    }

    @Override
    public IPostalAddress setDistrict(String district) {
        address.setDistrict(district);
        return this;
    }

    @Override
    public String getState() {
        return address.getState();
    }

    @Override
    public IPostalAddress setState(String state) {
        address.setState(state);
        return this;
    }

    @Override
    public String getPostalCode() {
        return address.getPostalCode();
    }

    @Override
    public IPostalAddress setPostalCode(String postalCode) {
        address.setPostalCode(postalCode);
        return this;
    }

    @Override
    public String getCountry() {
        return address.getCountry();
    }

    @Override
    public IPostalAddress setCountry(String country) {
        address.setCountry(country);
        return this;
    }

    @Override
    public IPeriod getPeriod() {
        return period;
    }

    @Override
    public AddressDt getWrapped() {
        return address;
    }
}
