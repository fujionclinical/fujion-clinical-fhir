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
package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.IAttachment;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Base64BinaryType;

public class AttachmentWrapper implements IAttachment, IWrapper<Attachment> {

    public static AttachmentWrapper wrap(Attachment attachment) {
        return attachment == null ? null : new AttachmentWrapper(attachment);
    }

    private final Attachment attachment;

    private AttachmentWrapper(Attachment attachment) {
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
        Base64BinaryType data = attachment.getDataElement();
        return data.isEmpty() ? null : data.getValueAsString();
    }

    @Override
    public void setEncodedData(String encodedData) {
        Base64BinaryType data = new Base64BinaryType();
        data.setValueAsString(encodedData);
        attachment.setDataElement(data);
    }

    @Override
    public byte[] getRawData() {
        Base64BinaryType data = attachment.getDataElement();
        return data.isEmpty() ? null : data.getValue();
    }

    @Override
    public void setRawData(byte[] rawData) {
        Base64BinaryType data = new Base64BinaryType();
        data.setValue(rawData);
        attachment.setDataElement(data);
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
    public Attachment getWrapped() {
        return attachment;
    }
}
