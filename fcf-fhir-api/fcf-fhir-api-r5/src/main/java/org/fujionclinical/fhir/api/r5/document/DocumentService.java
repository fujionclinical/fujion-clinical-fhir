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
package org.fujionclinical.fhir.api.r5.document;

import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import edu.utah.kmm.model.cool.clinical.finding.Document;
import edu.utah.kmm.model.cool.mediator.fhir.core.AbstractFhirDataSource;
import edu.utah.kmm.model.cool.mediator.fhir.r5.common.R5Utils;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.DocumentReference;
import org.hl7.fhir.r5.model.ValueSet;

import java.util.*;

/**
 * This is the documents api implementation.
 */
public class DocumentService {

    private final AbstractFhirDataSource dataSource;

    private static DocumentService instance;

    public static DocumentService getInstance() {
        return instance;
    }

    public DocumentService(AbstractFhirDataSource dataSource) {
        instance = this;
        this.dataSource = dataSource;
    }

    /**
     * Retrieves document references for a given patient.
     *
     * @param patientId The patient id.
     * @param startDate Start date for retrieval.
     * @param endDate   End date for retrieval.
     * @param type      Document type.
     * @return List of matching documents.
     */
    public List<Document> retrieveReferences(
            String patientId,
            Date startDate,
            Date endDate,
            String type) {
        ReferenceClientParam subject = new ReferenceClientParam(DocumentReference.SP_SUBJECT + ":Patient");

        IQuery<?> query = dataSource.getClient().search().forResource(DocumentReference.class)
                .where(subject.hasId(patientId));
        //.forResource("Patient/" + patient.getId().getIdPart() + "/DocumentReference");

        if (startDate != null) {
            query.where(DocumentReference.DATE.afterOrEquals().day(startDate));
        }

        if (endDate != null) {
            query.where(DocumentReference.DATE.beforeOrEquals().day(endDate));
        }

        if (type != null) {
            query.where(DocumentReference.TYPE.exactly().code(type));

        }

        Bundle bundle = query.returnBundle(Bundle.class).execute();
        List<DocumentReference> list = R5Utils.getEntries(bundle, DocumentReference.class);
        List<Document> results = new ArrayList<>(list.size());

        for (DocumentReference ref : list) {
            Document doc = null; //TODO: new Document(ref);
            results.add(doc);
        }
        return results;
    }

    public Collection<String> getTypes() {
        TreeSet<String> results = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        try {
            Bundle bundle = dataSource.getClient().search().forResource(ValueSet.class)
                    .where(ValueSet.NAME.matchesExactly().value("DocumentType")).returnBundle(Bundle.class).execute();

            for (CodeSystem cs : R5Utils.getEntries(bundle, CodeSystem.class)) {
                for (CodeSystem.ConceptDefinitionComponent concept : cs.getConcept()) {
                    results.add(concept.getDisplay());
                }
            }

        } catch (Exception e) {
            // NOP
        }

        return results;
    }

}
