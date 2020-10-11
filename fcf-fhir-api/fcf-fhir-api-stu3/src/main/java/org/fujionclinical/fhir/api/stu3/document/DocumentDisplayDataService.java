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

import edu.utah.kmm.model.cool.dao.query.QueryContext;
import org.fujionclinical.api.query.core.QueryUtil;
import org.fujionclinical.api.query.service.AbstractQueryServiceEx;
import org.fujionclinical.api.query.service.IQueryResult;

import java.util.List;

/**
 * Data service wrapper for retrieving document contents.
 */
public class DocumentDisplayDataService extends AbstractQueryServiceEx<DocumentService, Document> {


    public DocumentDisplayDataService(DocumentService service) {
        super(service);
    }

    @Override
    public IQueryResult<Document> fetch(QueryContext ctx) {
        @SuppressWarnings("unchecked")
        List<Document> documents = (List<Document>) ctx.getParam("documents");

        if (documents != null) {
            for (Document document : documents) {
                document.getContent();
            }
        }
        return QueryUtil.packageResult(documents);
    }

    @Override
    public boolean hasRequired(QueryContext context) {
        return true;
    }

}
