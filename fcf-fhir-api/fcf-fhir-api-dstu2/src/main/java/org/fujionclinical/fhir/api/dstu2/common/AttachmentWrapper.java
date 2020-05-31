package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.primitive.Base64BinaryDt;
import org.fujionclinical.api.model.IAttachment;
import org.fujionclinical.api.model.IWrapper;

public class AttachmentWrapper implements IAttachment, IWrapper<AttachmentDt> {

    public static AttachmentWrapper wrap(AttachmentDt attachment) {
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
