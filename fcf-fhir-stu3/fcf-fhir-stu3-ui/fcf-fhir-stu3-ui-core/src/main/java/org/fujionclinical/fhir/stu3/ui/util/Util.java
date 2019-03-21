/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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
package org.fujionclinical.fhir.stu3.ui.util;

import org.fujion.ancillary.MimeContent;
import org.fujion.component.Image;
import org.fujionclinical.ui.util.FCFUtil;
import org.hl7.fhir.dstu3.model.Attachment;

import java.util.List;

/**
 * FHIR utilities for UI.
 */
public class Util {

    public static final String RESOURCE_PATH = FCFUtil.getResourcePath(Util.class);

    public static final String SILHOUETTE_IMAGE = RESOURCE_PATH + "silhouette1.png";

    public static final String NOPATIENT_IMAGE = RESOURCE_PATH + "silhouette2.png";

    /**
     * Returns an image from a list of attachments.
     *
     * @param attachments List of attachments.
     * @return An image component if a suitable attachment was found, or null.
     */
    public static Image getImage(List<Attachment> attachments) {
        return getImage(attachments, null);
    }

    /**
     * Returns an image from a list of attachments.
     *
     * @param attachments List of attachments.
     * @param defaultImage URL of default image to use if none found (may be null).
     * @return An image component if a suitable attachment was found, or the default image if
     *         specified, or null.
     */
    public static Image getImage(List<Attachment> attachments, String defaultImage) {
        for (Attachment attachment : attachments) {
            String contentType = attachment.getContentType();

            if (contentType.startsWith("image/")) {
                try {
                    String url = attachment.getUrl();
                    return url != null ? getImage(url) : new Image(new MimeContent(contentType, attachment.getData()));
                } catch (Exception e) {

                }
            }
        }

        if (defaultImage != null) {
            try {
                return getImage(defaultImage);
            } catch (Exception e) {

            }
        }

        return null;
    }

    public static Image getImage(String url) {
        return new Image(url);
    }

    private Util() {
    }
}
