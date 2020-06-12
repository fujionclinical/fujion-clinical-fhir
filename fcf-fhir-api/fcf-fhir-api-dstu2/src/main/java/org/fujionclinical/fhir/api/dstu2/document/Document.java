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
package org.fujionclinical.fhir.api.dstu2.document;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import org.fujionclinical.api.model.core.DateTimeWrapper;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtilDstu2;

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
            CodingDt coding = FhirUtilDstu2.getFirst(documentReference.getType().getCoding());
            title = coding == null ? null : coding.getDisplay();
        }

        return title == null ? "" : title;
    }

    public DateTimeWrapper getDateTime() {
        return FhirUtil.convertDate(documentReference.getCreated());
    }

    public String getLocationName() {
        DocumentReference.Context ctx = documentReference.getContext();
        CodeableConceptDt facilityType = ctx == null ? null : ctx.getFacilityType();
        CodingDt coding = facilityType == null ? null : FhirUtilDstu2.getFirst(facilityType.getCoding());
        return coding == null ? "" : coding.getDisplay();
    }

    public String getAuthorName() {
        ResourceReferenceDt reference = FhirUtilDstu2.getFirst(documentReference.getAuthor());
        Practitioner author = DocumentService.getInstance().getResource(reference, Practitioner.class);
        return author == null ? "" : FhirUtilDstu2.formatName(author.getName());
    }

    public String getStatus() {
        CodeableConceptDt status = documentReference.getDocStatus();
        return status == null ? "" : FhirUtilDstu2.getDisplayValue(status);
    }

    public Collection<String> getTypes() {
        if (types == null) {
            types = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            CodeableConceptDt dt = documentReference.getType();
            List<CodingDt> codings = dt == null ? null : dt.getCoding();


            if (codings != null) {
                for (CodingDt coding : codings) {
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
        return FhirUtilDstu2.getFirst(getContent()).getContentType();
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
