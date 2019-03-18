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
package org.fujionclinical.fhir.r4.plugin.documents;

import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.BaseComponent;
import org.fujion.component.Import;
import org.fujion.event.Event;
import org.fujion.event.EventUtil;
import org.fujionclinical.shell.plugins.PluginController;
import org.fujionclinical.ui.controller.FrameworkController;

/**
 * This is the main controller for the clinical document display component. It doesn't do much other
 * than to control which of the two views (document list vs document display) is visible.
 */
public class DocumentMainController extends PluginController {

    private DocumentListController listController;

    private DocumentDisplayController displayController;

    @WiredComponent
    private Import documentList;

    @WiredComponent
    private Import documentDisplay;

    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        documentDisplay.setVisible(false);
        EventUtil.post("initImport", comp, null);
    }

    @EventHandler("initImport")
    private void onInitImport() {
        listController = (DocumentListController) initImport(documentList);
        displayController = (DocumentDisplayController) initImport(documentDisplay);
    }

    private Object initImport(Import cmp) {
        cmp.getFirstChild().addEventForward("viewOpen", root, null);
        return FrameworkController.getController(cmp.getFirstChild());
    }

    @EventHandler("viewOpen")
    private void onViewOpen(Event event) {
        boolean open = (Boolean) event.getData();
        displayController.setDocuments(!open ? null : listController.getSelectedDocuments());
        documentList.setVisible(!open);
        documentDisplay.setVisible(open);
    }
}
