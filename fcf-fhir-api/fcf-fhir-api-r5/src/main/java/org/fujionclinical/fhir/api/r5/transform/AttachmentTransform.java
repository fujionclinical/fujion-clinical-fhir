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
package org.fujionclinical.fhir.api.r5.transform;

import org.fujionclinical.api.model.core.IAttachment;
import org.fujionclinical.fhir.api.common.transform.AbstractDatatypeTransform;
import org.hl7.fhir.r5.model.Attachment;

public class AttachmentTransform extends AbstractDatatypeTransform<IAttachment, Attachment> {

    private static final AttachmentTransform instance = new AttachmentTransform();

    public static AttachmentTransform getInstance() {
        return instance;
    }

    private AttachmentTransform() {
        super(IAttachment.class, Attachment.class);
    }

    @Override
    public Attachment _fromLogicalModel(IAttachment src) {
        Attachment dest = new Attachment();
        dest.setContentType(src.getContentType());
        dest.setTitle(src.getTitle());
        dest.setData(src.getRawData());
        dest.setUrl(src.getURL());
        return dest;
    }

    @Override
    public IAttachment _toLogicalModel(Attachment src) {
        IAttachment dest = new org.fujionclinical.api.model.impl.Attachment();
        dest.setContentType(src.getContentType());
        dest.setTitle(src.getTitle());
        dest.setRawData(src.getData());
        dest.setURL(src.getUrl());
        return dest;
    }

}
