package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.primitive.Base64BinaryDt;
import org.fujionclinical.api.model.IAttachment;
import org.fujionclinical.api.model.IWrapper;

public class AttachmentWrapper implements IAttachment, IWrapper<AttachmentDt> {

    public static AttachmentWrapper create(AttachmentDt attachment) {
        return attachment == null ? null : new AttachmentWrapper(attachment);
    }

    private final AttachmentDt attachment;

    private AttachmentWrapper(AttachmentDt attachment) {
        this.attachment = attachment;
    }

    @Override
    public String getContentType() {
        return attachment.getContentType();
    }

    @Override
    public IAttachment setContentType(String contentType) {
        attachment.setContentType(contentType);
        return this;
    }

    @Override
    public String getTitle() {
        return attachment.getTitle();
    }

    @Override
    public IAttachment setTitle(String title) {
        attachment.setTitle(title);
        return this;
    }

    @Override
    public String getEncodedData() {
        Base64BinaryDt data = attachment.getDataElement();
        return data.isEmpty() ? null : data.getValueAsString();
    }

    @Override
    public IAttachment setEncodedData(String encodedData) {
        Base64BinaryDt data = new Base64BinaryDt();
        data.setValueAsString(encodedData);
        attachment.setData(data);
        return this;
    }

    @Override
    public byte[] getRawData() {
        Base64BinaryDt data = attachment.getDataElement();
        return data.isEmpty() ? null : data.getValue();
    }

    @Override
    public IAttachment setRawData(byte[] rawData) {
        Base64BinaryDt data = new Base64BinaryDt();
        data.setValue(rawData);
        attachment.setData(data);
        return this;
    }

    @Override
    public boolean hasData() {
        return !attachment.getDataElement().isEmpty();
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public IAttachment setURL(String url) {
        return null;
    }

    @Override
    public AttachmentDt getWrapped() {
        return null;
    }
}
