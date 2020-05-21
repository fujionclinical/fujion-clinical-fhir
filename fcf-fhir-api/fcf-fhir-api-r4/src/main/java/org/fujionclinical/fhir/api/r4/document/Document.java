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
package org.fujionclinical.fhir.api.r4.document;

import org.fujionclinical.fhir.api.r4.common.FhirUtil;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContextComponent;
import org.hl7.fhir.r4.model.DocumentReference.ReferredDocumentStatus;

import java.util.*;

/**
 * Model object wrapping a document reference and its contents (lazily loaded).
 */
public class Document implements Comparable<Document> {

    private DocumentReference documentReference;

    private Set<String> types;

    private List<DocumentContent> content;

    public Document(DocumentReference reference) {
        this.documentReference = reference;
    }

    public Document(
            DocumentReference reference,
            DocumentContent content) {
        this(reference);
        this.content = new ArrayList<>();
        this.content.add(content);
    }

    public DocumentReference getReference() {
        return documentReference;
    }

    protected void setReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    public String getTitle() {
        String title = documentReference.getDescription();

        if (title == null) {
            Coding coding = FhirUtil.getFirst(documentReference.getType().getCoding());
            title = coding == null ? null : coding.getDisplay();
        }

        return title == null ? "" : title;
    }

    public Date getDateTime() {
        return documentReference.getDate();
    }

    public String getLocationName() {
        DocumentReferenceContextComponent ctx = documentReference.getContext();
        CodeableConcept facilityType = ctx == null ? null : ctx.getFacilityType();
        Coding coding = facilityType == null ? null : FhirUtil.getFirst(facilityType.getCoding());
        return coding == null ? "" : coding.getDisplay();
    }

    public String getAuthorName() {
        Reference reference = documentReference.hasAuthor() ? FhirUtil.getFirst(documentReference.getAuthor()) : null;
        Practitioner author = DocumentService.getInstance().getResource(reference, Practitioner.class);
        return author == null ? "" : FhirUtil.formatName(author.getNameFirstRep());
    }

    public String getStatus() {
        ReferredDocumentStatus status = documentReference.getDocStatus();
        return status == null ? "" : status.getDisplay();
    }

    public Collection<String> getTypes() {
        if (types == null) {
            types = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            CodeableConcept dt = documentReference.getType();
            List<Coding> codings = dt == null ? null : dt.getCoding();

            if (codings != null) {
                for (Coding coding : codings) {
                    String type = coding.getDisplay();

                    if (type != null && !type.isEmpty()) {
                        types.add(type);
                    }
                }

            }
        }

        return types;
    }

    public boolean hasType(String type) {
        return getTypes().contains(type);
    }

    public String getContentType() {
        return FhirUtil.getFirst(getContent()).getContentType();
    }

    public List<DocumentContent> getContent() {
        if (content == null) {
            content = DocumentService.getInstance().getContent(documentReference);
        }

        return content;
    }

    @Override
    public int compareTo(Document document) {
        return getTitle().compareToIgnoreCase(document.getTitle());
    }
}
