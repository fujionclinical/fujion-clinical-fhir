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
package org.fujionclinical.fhir.api.r4.transform;

import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.core.IPostalAddress;
import org.fujionclinical.api.model.impl.PostalAddress;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r4.model.Address;

public class PostalAddressTransform extends AbstractDatatypeTransform<IPostalAddress, Address> {

    private static final PostalAddressTransform instance = new PostalAddressTransform();

    public static PostalAddressTransform getInstance() {
        return instance;
    }

    private PostalAddressTransform() {
        super(IPostalAddress.class, Address.class);
    }

    @Override
    public Address _fromLogicalModel(IPostalAddress src) {
        Address dest = new Address();
        dest.setPeriod(PeriodTransform.getInstance().fromLogicalModel(src.getPeriod()));
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), Address.AddressUse.class));
        dest.setType(CoreUtil.enumToEnum(src.getType(), Address.AddressType.class));
        dest.setCity(src.getCity());
        dest.setCountry(src.getCountry());
        dest.setDistrict(src.getDistrict());
        dest.setState(src.getState());
        dest.setPostalCode(src.getPostalCode());
        src.getLines().forEach(dest::addLine);
        return dest;
    }

    @Override
    public IPostalAddress _toLogicalModel(Address src) {
        IPostalAddress dest = new PostalAddress();
        dest.setPeriod(PeriodTransform.getInstance().toLogicalModel(src.getPeriod()));
        dest.setUse(CoreUtil.enumToEnum(src.getUse(), IPostalAddress.PostalAddressUse.class));
        dest.setType(CoreUtil.enumToEnum(src.getType(), IPostalAddress.PostalAddressType.class));
        dest.setCity(src.getCity());
        dest.setCountry(src.getCountry());
        dest.setDistrict(src.getDistrict());
        dest.setState(src.getState());
        dest.setPostalCode(src.getPostalCode());
        src.getLine().forEach(line -> dest.addLines(line.getValue()));
        return dest;
    }

}
