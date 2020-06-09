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
import org.fujionclinical.api.model.core.IAttachment;
import org.fujionclinical.api.model.core.IWrapper;

public class AttachmentWrapper implements IAttachment, IWrapper<AttachmentDt> {

    private final AttachmentDt attachment;

    protected AttachmentWrapper(AttachmentDt attachment) {
        this.attachment = attachment;
    }

    @Override
    public String getContentType() {
        return attachment.getContentType();
    }

    @Override
    public void setContentType(String contentType) {
        attachment.setContentType(contentType);
    }

    @Override
    public String getTitle() {
        return attachment.getTitle();
    }

    @Override
    public void setTitle(String title) {
        attachment.setTitle(title);
    }

    @Override
    public String getEncodedData() {
        Base64BinaryDt data = attachment.getDataElement();
        return data.isEmpty() ? null : data.getValueAsString();
    }

    @Override
    public void setEncodedData(String encodedData) {
        Base64BinaryDt data = new Base64BinaryDt();
        data.setValueAsString(encodedData);
        attachment.setData(data);
    }

    @Override
    public byte[] getRawData() {
        Base64BinaryDt data = attachment.getDataElement();
        return data.isEmpty() ? null : data.getValue();
    }

    @Override
    public void setRawData(byte[] rawData) {
        Base64BinaryDt data = new Base64BinaryDt();
        data.setValue(rawData);
        attachment.setData(data);
    }

    @Override
    public boolean hasData() {
        return !attachment.getDataElement().isEmpty();
    }

    @Override
    public String getURL() {
        return attachment.getUrl();
    }

    @Override
    public void setURL(String url) {
        attachment.setUrl(url);
    }

    @Override
    public AttachmentDt getWrapped() {
        return attachment;
    }
}
