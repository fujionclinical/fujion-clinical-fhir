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
package org.fujionclinical.fhir.stu3.plugin.scenario;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.fujion.ancillary.IResponseCallback;
import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.*;
import org.fujion.model.IComponentRenderer;
import org.fujion.model.IModelAndView;
import org.fujion.model.ListModel;
import org.fujion.page.PageUtil;
import org.fujionclinical.fhir.dstu3.api.common.BaseService;
import org.fujionclinical.fhir.dstu3.api.common.FhirUtil;
import org.fujionclinical.fhir.dstu3.api.scenario.Scenario;
import org.fujionclinical.ui.controller.FrameworkController;
import org.fujionclinical.ui.dialog.DialogUtil;
import org.fujionclinical.ui.util.FCFUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ViewResourcesController extends FrameworkController {
    
    private static IComponentRenderer<Row, IBaseResource> resourceRenderer = (resource) -> {
        Row row = new Row();
        row.addChild(new Cell(FhirUtil.getResourceIdPath(resource)));
        row.setData(resource);
        return row;
    };
    
    private static final Comparator<IBaseResource> resourceComparator = (r1, r2) -> {
        return r1.getIdElement().getValue().compareToIgnoreCase(r2.getIdElement().getValue());
    };
    
    @WiredComponent
    private Grid tblResources;
    
    @WiredComponent
    private Column colResource;
    
    @WiredComponent
    private Textbox txtResource;
    
    @WiredComponent
    private Radiobutton rbJSON;
    
    @WiredComponent
    private Button btnDelete;
    
    private Scenario scenario;
    
    private String title;
    
    private Window window;
    
    private final BaseService fhirService;
    
    private final ListModel<IBaseResource> model = new ListModel<>();
    
    /**
     * Display view resources dialog.
     *
     * @param scenario Scenario whose resources are to be viewed.
     * @param callback Callback upon dialog closure.
     */
    public static void show(Scenario scenario, IResponseCallback<Boolean> callback) {
        Map<String, Object> args = new HashMap<>();
        args.put("scenario", scenario);
        Window dlg = (Window) PageUtil.createPage("web/org/fujionclinical/fhir/ui/scenario/viewResources.fsp", null, args)
                .get(0);
        
        dlg.modal((event) -> {
            if (callback != null) {
                callback.onComplete(dlg.hasAttribute("modified"));
            }
        });
    }
    
    public ViewResourcesController(BaseService fhirService) {
        super();
        this.fhirService = fhirService;
    }
    
    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        window = (Window) comp;
        title = window.getTitle();
        scenario = (Scenario) comp.getAttribute("scenario");
        model.addAll(scenario.getResources());
        model.sort(resourceComparator, true);
        colResource.setSortComparator(resourceComparator);
        IModelAndView<Row, IBaseResource> mv = tblResources.getRows().getModelAndView(IBaseResource.class);
        mv.setRenderer(resourceRenderer);
        mv.setModel(model);
        updateCaption();
    }
    
    @EventHandler(value = "change", target = "@lboxResources")
    private void onSelect$lboxResources() {
        displayResource();
    }
    
    @EventHandler(value = "change", target = "@rbJSON")
    private void onChange$rbJSON() {
        displayResource();
    }
    
    @EventHandler(value = "click", target = "@btnDelete")
    private void onClick$btnDelete() {
        IBaseResource resource = getSelectedResource();
        
        DialogUtil.confirm("Delete " + FhirUtil.getResourceIdPath(resource, true) + "?", "Delete Resource", (confirm) -> {
            if (confirm) {
                try {
                    fhirService.deleteResource(resource);
                    model.remove(resource);
                    root.setAttribute("modified", true);
                    updateCaption();
                    displayResource();
                } catch (Exception e) {
                    DialogUtil.showError("Error deleting resource:\n\n" + FCFUtil.formatExceptionForDisplay(e));
                }
            }
        });
    }
    
    private void updateCaption() {
        window.setTitle(
            title + " - " + scenario.getName() + " (" + model.size() + " resource" + (model.size() == 1 ? ")" : "s)"));
    }
    
    private IBaseResource getSelectedResource() {
        Row row = tblResources.getRows().getSelectedRow();
        return row == null ? null : (IBaseResource) row.getData();
    }
    
    private void displayResource() {
        IBaseResource resource = getSelectedResource();
        
        if (resource == null) {
            txtResource.setValue(null);
            btnDelete.setDisabled(true);
        } else {
            FhirContext ctx = fhirService.getClient().getFhirContext();
            IParser parser = rbJSON.isChecked() ? ctx.newJsonParser() : ctx.newXmlParser();
            parser.setPrettyPrint(true);
            txtResource.setValue(parser.encodeResourceToString(resource));
            txtResource.selectRange(0, 0);
            btnDelete.setDisabled(false);
        }
    }
    
}
