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

    private final AddressDt address;

    private final PeriodWrapper period;

    public static PostalAddressWrapper wrap(AddressDt address) {
        return address == null ? null : new PostalAddressWrapper(address);
    }

    public static List<IPostalAddress> wrap(List<AddressDt> addresses) {
        return addresses == null ? null : addresses.stream().map(address -> PostalAddressWrapper.wrap(address)).collect(Collectors.toList());
    }

    public static List<AddressDt> unwrap(List<IWrapper<AddressDt>> addresses) {
        return addresses == null ? null : addresses.stream().map(address -> address.getWrapped()).collect(Collectors.toList());
    }

    private PostalAddressWrapper(AddressDt address) {
        this.address = address;
        this.period = PeriodWrapper.wrap(address.getPeriod());
    }

    @Override
    public PostalAddressUse getUse() {
        AddressUseEnum use = address.getUseElement().isEmpty() ? null : address.getUseElement().getValueAsEnum();
        return FhirUtilDstu2.convertEnum(use, PostalAddressUse.class);
    }

    @Override
    public void setUse(PostalAddressUse use) {
        address.setUse(FhirUtilDstu2.convertEnum(use, AddressUseEnum.class));
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
    public AddressDt getWrapped() {
        return address;
    }

}
