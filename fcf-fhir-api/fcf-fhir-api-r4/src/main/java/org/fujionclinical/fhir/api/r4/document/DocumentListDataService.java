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

import edu.utah.kmm.model.cool.clinical.finding.Document;
import edu.utah.kmm.model.cool.foundation.entity.Person;
import edu.utah.kmm.model.cool.mediator.query.QueryContext;
import org.fujion.common.DateRange;
import org.fujionclinical.api.query.core.QueryUtil;
import org.fujionclinical.api.query.service.AbstractQueryServiceEx;
import org.fujionclinical.api.query.service.IQueryResult;

import java.util.Date;

/**
 * Data service wrapper for documents service.
 */
public class DocumentListDataService extends AbstractQueryServiceEx<Document> {

    private final DocumentService service;

    public DocumentListDataService(DocumentService service) {
        super(service.getDataSource());
        this.service = service;
    }

    @Override
    public IQueryResult<Document> fetch(QueryContext context) {
        DateRange dateRange = (DateRange) context.getParam("dateRange");
        Date startDate = dateRange.getStartDate();
        Date endDate = dateRange.getEndDate();
        Person patient = (Person) context.getParam("patient");
        return QueryUtil
                .packageResult(service.retrieveReferences(patient.getDefaultId().getId(), startDate, endDate, (String) context.getParam("type")));
    }

    @Override
    public boolean hasRequired(QueryContext context) {
        return true;
    }

}
