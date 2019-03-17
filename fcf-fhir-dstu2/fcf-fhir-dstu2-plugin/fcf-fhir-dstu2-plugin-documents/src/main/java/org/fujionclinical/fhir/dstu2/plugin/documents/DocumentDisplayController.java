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
package org.fujionclinical.fhir.dstu2.plugin.documents;

import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.Combobox;
import org.fujion.component.Label;
import org.fujion.component.Row;
import org.fujion.event.EventUtil;
import org.fujion.model.IListModel;
import org.fujionclinical.api.query.DateQueryFilter.DateType;
import org.fujionclinical.api.query.IQueryContext;
import org.fujionclinical.fhir.dstu2.api.document.Document;
import org.fujionclinical.fhir.dstu2.api.document.DocumentDisplayDataService;
import org.fujionclinical.fhir.dstu2.api.document.DocumentService;
import org.fujionclinical.fhir.dstu2.ui.reports.controller.PatientQueryParameter;
import org.fujionclinical.ui.reports.controller.AbstractGridController;

import java.util.Date;
import java.util.List;

/**
 * Controller for displaying the contents of selected documents.
 */
public class DocumentDisplayController extends AbstractGridController<Document, Document> {
    
    private List<Document> documents;
    
    @WiredComponent
    private Label lblInfo;
    
    @WiredComponent
    private Combobox cboHeader;
    
    private final DocumentDisplayComboRenderer comboRenderer = new DocumentDisplayComboRenderer();
    
    public DocumentDisplayController(DocumentService service) {
        super(new DocumentDisplayDataService(service), "fcfdocuments", "DOCUMENT", "documentsPrint.css", "patient", new PatientQueryParameter());
    }
    
    @Override
    protected void initializeController() {
        super.initializeController();
        cboHeader.setRenderer(comboRenderer);
        grid.getRows().setRenderer(new DocumentDisplayRenderer());
    }
    
    @Override
    protected void prepareQueryContext(IQueryContext context) {
        context.setParam("documents", documents);
    }
    
    /**
     * This view should be closed when the patient context changes.
     */
    @Override
    protected void onParameterChanged(SupplementalQueryParam<?> param) {
        closeView();
    }
    
    /**
     * Suppress data fetch if there are no documents in the view.
     */
    @Override
    protected void fetchData() {
        if (documents != null) {
            super.fetchData();
        }
    }
    
    /**
     * Scroll to document with same header.
     */
    @EventHandler(value = "change", target = "@cboHeader")
    private void onChange$cboHeader() {
        Document doc = (Document) cboHeader.getSelectedItem().getData();
        
        for (Row row : grid.getRows().getChildren(Row.class)) {
            Document doc2 = (Document) row.getData();
            
            if (doc == doc2) {
                row.scrollIntoView();
                break;
            }
        }
    }
    
    /**
     * Clears any displayed documents and reverts to document selection mode.
     */
    @EventHandler(value = "click", target = "btnReturn")
    private void onClick$btnReturn() {
        closeView();
    }
    
    /**
     * Not really needed.
     */
    @Override
    public Date getDateByType(Document result, DateType dateType) {
        return result.getDateTime();
    }
    
    /**
     * Clears any displayed documents and reverts to document selection mode.
     */
    protected void closeView() {
        documents = null;
        setModel(null);
        EventUtil.post("viewOpen", root, false);
    }
    
    /**
     * Sets the documents to be displayed and updates the displayed count.
     *
     * @param documents The documents to be displayed.
     */
    protected void setDocuments(List<Document> documents) {
        this.documents = documents;
        int docCount = documents == null ? 0 : documents.size();
        lblInfo.setLabel(docCount + " document(s)");
        refresh();
    }
    
    /**
     * Updates the header selector when the model changes.
     */
    @Override
    protected void setModel(IListModel<Document> model) {
        super.setModel(model);
        cboHeader.setModel(model);
        cboHeader.setValue(null);
    }
    
    @Override
    protected List<Document> toModel(List<Document> results) {
        return results;
    }
    
}
