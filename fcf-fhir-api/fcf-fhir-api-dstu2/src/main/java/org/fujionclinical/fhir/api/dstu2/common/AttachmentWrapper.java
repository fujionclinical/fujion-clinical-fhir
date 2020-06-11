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

import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.primitive.Base64BinaryDt;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IAttachment;

public class AttachmentWrapper extends AbstractWrapper<AttachmentDt> implements IAttachment {

    protected AttachmentWrapper(AttachmentDt attachment) {
        super(attachment);
    }

    @Override
    public String getContentType() {
        return getWrapped().getContentType();
    }

    @Override
    public void setContentType(String contentType) {
        getWrapped().setContentType(contentType);
    }

    @Override
    public String getTitle() {
        return getWrapped().getTitle();
    }

    @Override
    public void setTitle(String title) {
        getWrapped().setTitle(title);
    }

    @Override
    public String getEncodedData() {
        Base64BinaryDt data = getWrapped().getDataElement();
        return data.isEmpty() ? null : data.getValueAsString();
    }

    @Override
    public void setEncodedData(String encodedData) {
        Base64BinaryDt data = new Base64BinaryDt();
        data.setValueAsString(encodedData);
        getWrapped().setData(data);
    }

    @Override
    public byte[] getRawData() {
        Base64BinaryDt data = getWrapped().getDataElement();
        return data.isEmpty() ? null : data.getValue();
    }

    @Override
    public void setRawData(byte[] rawData) {
        Base64BinaryDt data = new Base64BinaryDt();
        data.setValue(rawData);
        getWrapped().setData(data);
    }

    @Override
    public boolean hasData() {
        return !getWrapped().getDataElement().isEmpty();
    }

    @Override
    public String getURL() {
        return getWrapped().getUrl();
    }

    @Override
    public void setURL(String url) {
        getWrapped().setUrl(url);
    }

}
