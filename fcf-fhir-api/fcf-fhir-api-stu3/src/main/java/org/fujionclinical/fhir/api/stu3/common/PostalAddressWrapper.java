/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.core.IPostalAddress;
import org.fujionclinical.api.model.core.IWrapper;
import org.hl7.fhir.dstu3.model.Address;

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
        return FhirUtilStu3.convertEnum(address.getUse(), PostalAddressUse.class);
    }

    @Override
    public void setUse(PostalAddressUse use) {
        address.setUse(FhirUtilStu3.convertEnum(use, Address.AddressUse.class));
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
