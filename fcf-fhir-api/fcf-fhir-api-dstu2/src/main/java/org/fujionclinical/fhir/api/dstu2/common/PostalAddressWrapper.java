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

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IPeriod;
import org.fujionclinical.api.model.core.IPostalAddress;
import org.fujionclinical.api.model.core.WrappedList;
import org.fujionclinical.fhir.api.common.core.FhirUtil;

import java.util.List;

public class PostalAddressWrapper extends AbstractWrapper<AddressDt> implements IPostalAddress {

    private final IPeriod period;

    private final List<String> lines;

    protected PostalAddressWrapper(AddressDt address) {
        super(address);
        this.period = PeriodTransform.getInstance().wrap(address.getPeriod());
        this.lines = new WrappedList<>(address.getLine(), StringTransform.getInstance());
    }

    @Override
    public PostalAddressUse getUse() {
        AddressUseEnum use = getWrapped().getUseElement().isEmpty() ? null : getWrapped().getUseElement().getValueAsEnum();
        return FhirUtil.convertEnum(use, PostalAddressUse.class);
    }

    @Override
    public void setUse(PostalAddressUse use) {
        getWrapped().setUse(FhirUtil.convertEnum(use, AddressUseEnum.class));
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public String getCity() {
        return getWrapped().getCity();
    }

    @Override
    public void setCity(String city) {
        getWrapped().setCity(city);
    }

    @Override
    public String getDistrict() {
        return getWrapped().getDistrict();
    }

    @Override
    public void setDistrict(String district) {
        getWrapped().setDistrict(district);
    }

    @Override
    public String getState() {
        return getWrapped().getState();
    }

    @Override
    public void setState(String state) {
        getWrapped().setState(state);
    }

    @Override
    public String getPostalCode() {
        return getWrapped().getPostalCode();
    }

    @Override
    public void setPostalCode(String postalCode) {
        getWrapped().setPostalCode(postalCode);
    }

    @Override
    public String getCountry() {
        return getWrapped().getCountry();
    }

    @Override
    public void setCountry(String country) {
        getWrapped().setCountry(country);
    }

    @Override
    public IPeriod getPeriod() {
        return period;
    }

}
