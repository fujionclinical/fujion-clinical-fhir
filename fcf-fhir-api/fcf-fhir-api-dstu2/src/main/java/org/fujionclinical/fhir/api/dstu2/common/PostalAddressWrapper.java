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
