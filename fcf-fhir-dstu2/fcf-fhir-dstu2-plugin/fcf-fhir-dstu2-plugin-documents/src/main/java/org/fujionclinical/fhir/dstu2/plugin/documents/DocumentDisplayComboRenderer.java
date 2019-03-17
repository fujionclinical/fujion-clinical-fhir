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

import org.fujion.component.Comboitem;
import org.fujion.model.IComponentRenderer;
import org.fujionclinical.fhir.dstu2.api.document.Document;

/**
 * Renderer for the document display combo box selector.
 */
public class DocumentDisplayComboRenderer implements IComponentRenderer<Comboitem, Document> {

    /**
     * Render the combo item for the specified document.
     *
     * @param doc The document associated with the list item.
     */
    @Override
    public Comboitem render(Document doc) {
        Comboitem item = new Comboitem(doc.getTitle());
        item.setData(doc);
        return item;
    }
}
