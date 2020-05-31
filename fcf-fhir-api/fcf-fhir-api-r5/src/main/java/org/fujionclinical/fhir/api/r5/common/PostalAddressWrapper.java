package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IPostalAddress;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r5.model.Address;

import java.util.List;
import java.util.stream.Collectors;

public class PostalAddressWrapper implements IPostalAddress, IWrapper<Address> {

    private final Address address;

    private final PeriodWrapper period;

    public static PostalAddressWrapper wrap(Address address) {
        return address == null ? null : new PostalAddressWrapper(address);
    }

    public static List<IPostalAddress> wrap(List<Address> addresses) {
        return addresses == null ? null : addresses.stream().map(address -> PostalAddressWrapper.wrap(address)).collect(Collectors.toList());
    }

    public static List<Address> unwrap(List<IWrapper<Address>> addresses) {
        return addresses == null ? null : addresses.stream().map(address -> address.getWrapped()).collect(Collectors.toList());
    }

    private PostalAddressWrapper(Address address) {
        this.address = address;
        this.period = PeriodWrapper.wrap(address.getPeriod());
    }

    @Override
    public PostalAddressUse getUse() {
        return FhirUtilR5.convertEnum(address.getUse(), PostalAddressUse.class);
    }

    @Override
    public void setUse(PostalAddressUse use) {
        address.setUse(FhirUtilR5.convertEnum(use, Address.AddressUse.class));
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
    public void setCity(String city) {
        address.setCity(city);
    }

    @Override
    public String getDistrict() {
        return address.getDistrict();
    }

    @Override
    public void setDistrict(String district) {
        address.setDistrict(district);
    }

    @Override
    public String getState() {
        return address.getState();
    }

    @Override
    public void setState(String state) {
        address.setState(state);
    }

    @Override
    public String getPostalCode() {
        return address.getPostalCode();
    }

    @Override
    public void setPostalCode(String postalCode) {
        address.setPostalCode(postalCode);
    }

    @Override
    public String getCountry() {
        return address.getCountry();
    }

    @Override
    public void setCountry(String country) {
        address.setCountry(country);
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
