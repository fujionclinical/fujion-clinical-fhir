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
package org.fujionclinical.fhir.api.stu3.document;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import org.fujionclinical.fhir.api.stu3.common.BaseService;
import org.fujionclinical.fhir.api.stu3.common.FhirUtilStu3;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.dstu3.model.DocumentReference.DocumentReferenceContentComponent;

import java.util.*;

/**
 * This is the documents api implementation.
 */
public class DocumentService extends BaseService {

    private static DocumentService instance;

    public static DocumentService getInstance() {
        return instance;
    }

    public DocumentService(IGenericClient client) {
        super(client);
        instance = this;
    }

    /**
     * Retrieves document contents, regardless of type.
     *
     * @param documentReference A document reference.
     * @return List of document contents, never null.
     */
    public List<DocumentContent> getContent(DocumentReference documentReference) {
        List<DocumentContent> contents = new ArrayList<>();

        for (DocumentReferenceContentComponent content : documentReference.getContent()) {
            Attachment attachment = content.getAttachment();
            byte[] data = attachment.getData();
            String type = attachment.getContentType();
            String url = attachment.getUrl();

            if ((data == null || data.length == 0) && url != null) {
                try {
                    data = getClient().read().resource(Binary.class).withUrl(url).execute().getContent();
                } catch (Exception e) {
                    data = e.getMessage().getBytes();
                    type = "text/x-error";
                }
            }

            if (data != null) {
                contents.add(new DocumentContent(data, type));
            }
        }

        return contents;
    }

    /**
     * Updates or creates the document.
     *
     * @param document Document to update.
     */
    public void updateDocument(Document document) {
        DocumentReference ref = document.getReference();
        ref.getContent().clear();

        for (DocumentContent content : document.getContent()) {
            if (!content.isEmpty()) {
                Attachment attachment = new Attachment();
                attachment.setContentType(content.getContentType());
                attachment.setData(content.getData());
                DocumentReferenceContentComponent cc = new DocumentReferenceContentComponent(attachment);
                ref.getContent().add(cc);
            }
        }

        ref = this.createOrUpdateResource(ref);

        if (ref != null) {
            document.setReference(ref);
        }

    }

    /**
     * Retrieves document references for a given patient.
     *
     * @param patient   Patient whose documents are to be retrieved.
     * @param startDate Start date for retrieval.
     * @param endDate   End date for retrieval.
     * @param type      Document type.
     * @return List of matching documents.
     */
    public List<Document> retrieveReferences(
            Patient patient,
            Date startDate,
            Date endDate,
            String type) {
        ReferenceClientParam subject = new ReferenceClientParam(DocumentReference.SP_SUBJECT + ":Patient");

        IQuery<?> query = getClient().search().forResource(DocumentReference.class)
                .where(subject.hasId(patient.getIdElement().getIdPart()));
        //.forResource("Patient/" + patient.getId().getIdPart() + "/DocumentReference");

        if (startDate != null) {
            query.where(DocumentReference.CREATED.afterOrEquals().day(startDate));
        }

        if (endDate != null) {
            query.where(DocumentReference.CREATED.beforeOrEquals().day(endDate));
        }

        if (type != null) {
            query.where(DocumentReference.TYPE.exactly().code(type));

        }

        Bundle bundle = query.returnBundle(Bundle.class).execute();
        List<DocumentReference> list = FhirUtilStu3.getEntries(bundle, DocumentReference.class);
        List<Document> results = new ArrayList<>(list.size());

        for (DocumentReference ref : list) {
            Document doc = new Document(ref);
            results.add(doc);
        }
        return results;
    }

    public Collection<String> getTypes() {
        TreeSet<String> results = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        try {
            Bundle bundle = getClient().search().forResource(CodeSystem.class)
                    .where(CodeSystem.NAME.matchesExactly().value("DocumentType")).returnBundle(Bundle.class).execute();

            for (CodeSystem cs : FhirUtilStu3.getEntries(bundle, CodeSystem.class)) {
                for (ConceptDefinitionComponent concept : cs.getConcept()) {
                    results.add(concept.getDisplay());
                }
            }

        } catch (Exception e) {
        }

        return results;
    }
}
