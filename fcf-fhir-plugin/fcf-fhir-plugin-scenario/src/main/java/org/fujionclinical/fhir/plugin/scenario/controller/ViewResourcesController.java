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
package org.fujionclinical.fhir.plugin.scenario.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.coolmodel.mediator.datasource.DataSources;
import org.coolmodel.mediator.fhir.common.AbstractFhirDataSource;
import org.coolmodel.mediator.fhir.common.FhirUtils;
import org.fujion.ancillary.IResponseCallback;
import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.MiscUtil;
import org.fujion.component.*;
import org.fujion.dialog.DialogUtil;
import org.fujion.model.IComponentRenderer;
import org.fujion.model.IModelAndView;
import org.fujion.model.ListModel;
import org.fujion.page.PageUtil;
import org.fujionclinical.fhir.scenario.common.ScenarioBase;
import org.fujionclinical.ui.controller.FrameworkController;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ViewResourcesController extends FrameworkController {

    private static final Comparator<IBaseResource> resourceComparator = (r1, r2) -> r1.getIdElement().getValue().compareToIgnoreCase(r2.getIdElement().getValue());

    private static final IComponentRenderer<Row, IBaseResource> resourceRenderer = (resource) -> {
        Row row = new Row();
        row.addChild(new Cell(FhirUtils.getResourceIdPath(resource)));
        row.setData(resource);
        return row;
    };

    private final AbstractFhirDataSource data;

    private final ListModel<IBaseResource> model = new ListModel<>();

    @WiredComponent
    private Grid grdResources;

    @WiredComponent
    @SuppressWarnings("unused")
    private Rows rowsResources;

    @WiredComponent
    private Column colResource;

    @WiredComponent
    private Memobox txtResource;

    @WiredComponent
    private Radiobutton rbJSON;

    @WiredComponent
    private Button btnDelete;

    private ScenarioBase scenario;

    private String title;

    private Window window;

    /**
     * Display view resources dialog.
     *
     * @param scenario Scenario whose resources are to be viewed.
     * @param callback Callback upon dialog closure.
     */
    public static void show(
            ScenarioBase scenario,
            IResponseCallback<Boolean> callback) {
        Map<String, Object> args = new HashMap<>();
        args.put("scenario", scenario);
        Window dlg = (Window) PageUtil.createPage("web/org/fujionclinical/fhir/plugin/scenario/viewResources.fsp", null, args)
                .get(0);

        dlg.modal((event) -> {
            if (callback != null) {
                callback.onComplete(dlg.hasAttribute("modified"));
            }
        });
    }

    public ViewResourcesController(String dataSourceId) {
        super();
        this.data = (AbstractFhirDataSource) DataSources.get(dataSourceId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        window = (Window) comp;
        title = window.getTitle();
        scenario = (ScenarioBase) comp.getAttribute("scenario");
        model.addAll(scenario.getResources());
        model.sort(resourceComparator, true);
        colResource.setSortComparator(resourceComparator);
        IModelAndView<Row, IBaseResource> mv = grdResources.getRows().getModelAndView(IBaseResource.class);
        mv.setRenderer(resourceRenderer);
        mv.setModel(model);
        updateCaption();
    }

    @EventHandler(value = "change", target = "@rowsResources")
    private void onSelect$rowsResources() {
        displayResource();
    }

    @EventHandler(value = "change", target = "@rbJSON")
    private void onChange$rbJSON() {
        displayResource();
    }

    @EventHandler(value = "click", target = "@btnDelete")
    private void onClick$btnDelete() {
        IBaseResource resource = getSelectedResource();

        if (resource == null) {
            return;
        }

        DialogUtil.confirm("Delete " + FhirUtils.getResourceIdPath(resource, true) + "?", "Delete Resource", (confirm) -> {
            if (confirm) {
                try {
                    data.deleteResource(resource);
                    model.remove(resource);
                    root.setAttribute("modified", true);
                    updateCaption();
                    displayResource();
                } catch (Exception e) {
                    DialogUtil.showError("Error deleting resource:\n\n" + MiscUtil.formatExceptionForDisplay(e));
                }
            }
        });
    }

    private void updateCaption() {
        window.setTitle(
                title + " - " + scenario.getName() + " (" + model.size() + " resource" + (model.size() == 1 ? ")" : "s)"));
    }

    private IBaseResource getSelectedResource() {
        Row row = grdResources.getRows().getSelectedRow();
        return row == null ? null : (IBaseResource) row.getData();
    }

    private void displayResource() {
        IBaseResource resource = getSelectedResource();

        if (resource == null) {
            txtResource.setValue(null);
            btnDelete.setDisabled(true);
        } else {
            FhirContext ctx = data.getClient().getFhirContext();
            IParser parser = rbJSON.isChecked() ? ctx.newJsonParser() : ctx.newXmlParser();
            parser.setPrettyPrint(true);
            txtResource.setValue(parser.encodeResourceToString(resource));
            txtResource.selectRange(0, 0);
            btnDelete.setDisabled(false);
        }
    }

}
