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
package org.fujionclinical.fhir.plugin.documents.dstu2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.component.BaseComponent;
import org.fujion.component.Cell;
import org.fujion.component.Grid;
import org.fujion.component.Row;
import org.fujion.event.DblclickEvent;
import org.fujion.model.IComponentRenderer;
import org.fujionclinical.fhir.api.dstu2.document.Document;

/**
 * Renderer for the document list.
 */
public class DocumentListRenderer implements IComponentRenderer<Row, Document> {
    
    private static final Log log = LogFactory.getLog(DocumentListRenderer.class);
    
    private final Grid grid;
    
    public DocumentListRenderer(Grid grid) {
        this.grid = grid;
    }
    
    /**
     * Render the grid row for the specified document.
     *
     * @param doc The document associated with the list item.
     * @return The rendered row.
     */
    @Override
    public Row render(Document doc) {
        Row row = new Row();
        row.setData(doc);
        log.trace("item render");
        row.addEventForward(DblclickEvent.TYPE, grid, null);
        addCell(row, "");
        addCell(row, doc.getDateTime());
        addCell(row, doc.getTitle());
        addCell(row, doc.getLocationName());
        addCell(row, doc.getAuthorName());
        return row;
    }
    
    /**
     * Add a cell to the list item containing the specified text value.
     *
     * @param parent Parent component to receive new cell.
     * @param value Text to include in the new cell.
     */
    private void addCell(BaseComponent parent, Object value) {
        parent.addChild(new Cell(value == null ? null : value.toString()));
    }
    
}
