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
package org.fujionclinical.fhir.plugin.documents;

import org.fujion.ancillary.MimeContent;
import org.fujion.component.Cell;
import org.fujion.component.Div;
import org.fujion.component.Html;
import org.fujion.component.Iframe;
import org.fujion.component.Label;
import org.fujion.component.Row;
import org.fujion.model.IComponentRenderer;
import org.fujionclinical.fhir.api.document.Document;
import org.fujionclinical.fhir.api.document.DocumentContent;
import org.fujionclinical.fhir.ui.reporting.Constants;

/**
 * Renderer for the document display.
 */
public class DocumentDisplayRenderer implements IComponentRenderer<Row, Document> {
    
    /**
     * Render the list item for the specified document.
     *
     * @param doc The document associated with the list item.
     */
    @Override
    public Row render(Document doc) {
        Row row = new Row();
        row.setData(doc);
        Cell cell = new Cell();
        row.addChild(cell);
        Div sep = new Div();
        sep.addClass("fcf-documents-sep");
        cell.addChild(sep);
        Div div = new Div();
        div.addClass(Constants.SCLASS_TEXT_REPORT_TITLE);
        cell.addChild(div);
        Div boxHeader = new Div();
        div.addClass("fujion-layout-horizontal");
        Label header = new Label(doc.getTitle());
        header.addClass(Constants.SCLASS_TEXT_REPORT_TITLE);
        boxHeader.addChild(header);
        div.addChild(boxHeader);
        
        for (DocumentContent content : doc.getContent()) {
            if (content.getContentType().equals("text/html")) {
                Html html = new Html();
                html.setContent(content.toString());
                cell.addChild(html);
            } else if (content.getContentType().equals("text/plain")) {
                Label lbl = new Label(content.toString());
                cell.addChild(lbl);
            } else {
                MimeContent data = new MimeContent(content.getContentType(), content.getData());
                Iframe frame = new Iframe();
                frame.setContent(data);
                cell.addChild(frame);
            }
        }
        
        return row;
    }
    
}
