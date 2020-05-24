package org.fujionclinical.fhir.api.stu3.common;

import org.fujionclinical.api.model.IAttachment;
import org.fujionclinical.api.model.IWrapper;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.Base64BinaryType;

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
        Base64BinaryType data = attachment.getDataElement();
        return data.isEmpty() ? null : data.getValueAsString();
    }

    @Override
    public IAttachment setEncodedData(String encodedData) {
        Base64BinaryType data = new Base64BinaryType();
        data.setValueAsString(encodedData);
        attachment.setDataElement(data);
        return this;
    }

    @Override
    public byte[] getRawData() {
        Base64BinaryType data = attachment.getDataElement();
        return data.isEmpty() ? null : data.getValue();
    }

    @Override
    public IAttachment setRawData(byte[] rawData) {
        Base64BinaryType data = new Base64BinaryType();
        data.setValue(rawData);
        attachment.setDataElement(data);
        return this;
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
    public IAttachment setURL(String url) {
        attachment.setUrl(url);
        return this;
    }

    @Override
    public Attachment getWrapped() {
        return attachment;
    }
}
