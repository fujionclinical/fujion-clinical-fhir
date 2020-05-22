package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IPostalAddress;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r5.model.Address;

import java.util.List;
import java.util.stream.Collectors;

public class PostalAddressWrapper implements IPostalAddress, IWrapper<Address> {

    public static PostalAddressWrapper create(Address address) {
        return address == null ? null : new PostalAddressWrapper(address);
    }

    public static List<IPostalAddress> wrap(List<Address> addresses) {
        return addresses.stream().map(address -> PostalAddressWrapper.create(address)).collect(Collectors.toList());
    }

    public static List<Address> unwrap(List<IWrapper<Address>> addresses) {
        return addresses.stream().map(address -> address.getWrapped()).collect(Collectors.toList());
    }

    private final Address address;

    private final PeriodWrapper period;

    private PostalAddressWrapper(Address address) {
        this.address = address;
        this.period = PeriodWrapper.create(address.getPeriod());
    }

    @Override
    public PostalAddressUse getUse() {
        return FhirUtil.convertEnum(address.getUse(), PostalAddressUse.class);
    }

    @Override
    public IPostalAddress setUse(PostalAddressUse use) {
        address.setUse(FhirUtil.convertEnum(use, Address.AddressUse.class));
        return this;
    }

    @Override
    public List<String> getLines() {
        return address.getLine().stream()
                .map(line -> line.toString())
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
    public Address getWrapped() {
        return address;
    }
}
