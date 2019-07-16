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
package org.fujionclinical.fhir.plugin.scenario.r4.controller;

import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.StrUtil;
import org.fujion.component.*;
import org.fujion.event.ChangeEvent;
import org.fujion.event.Event;
import org.fujion.event.EventUtil;
import org.fujion.model.IComponentRenderer;
import org.fujion.model.IModelAndView;
import org.fujion.model.ListModel;
import org.fujionclinical.api.context.ISurveyResponse;
import org.fujionclinical.fhir.plugin.scenario.r4.api.Scenario;
import org.fujionclinical.fhir.plugin.scenario.r4.api.ScenarioContext;
import org.fujionclinical.fhir.plugin.scenario.r4.api.ScenarioRegistry;
import org.fujionclinical.shell.plugins.PluginController;
import org.fujionclinical.ui.dialog.DialogUtil;
import org.fujionclinical.ui.util.FCFUtil;

import java.util.Collections;
import java.util.Comparator;

/**
 * This controller is only intended to be used for demo purposes in order to stage and unstage data.
 */
public class ScenarioManagerController extends PluginController implements ScenarioContext.IScenarioContextEvent {
    
    private static final Comparator<Scenario> scenarioComparator = (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName());
    
    @WiredComponent
    private Combobox cboScenarios;
    
    @WiredComponent
    private Label lblMessage;
    
    @WiredComponent
    private BaseComponent scenarioButtons;

    @WiredComponent
    private Toolbar toolbar;

    private Scenario activeScenario;
    
    private final ScenarioRegistry scenarioRegistry;
    
    private final ListModel<Scenario> model = new ListModel<>();
    
    private IModelAndView<Comboitem, Scenario> mv;
    
    private final IComponentRenderer<Comboitem, Scenario> scenarioRenderer = (scenario) -> {
        boolean active = activeScenario == scenario;
        Comboitem item = new Comboitem();
        item.setLabel(scenario.getName() + (active ? " (active)" : ""));
        item.setData(scenario);
        
        if (active) {
            item.addStyles("font-weight: bold; color: blue!important");
            EventUtil.post(ChangeEvent.TYPE, cboScenarios, item);
        }
        
        return item;
        
    };
    
    private enum Action {
        LOAD("Loading scenario"), RELOAD("Reloading scenario"), RESET("Resetting scenario"), DELETE(
                "Deleting scenario"), DELETEALL("Deleting resources across all scenarios");
        
        private final String label;
        
        Action(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    /**
     * Demonstration Configuration Helper Class.
     */
    public static void show() {
        DialogUtil.popup("web/org/fujionclinical/fhir/plugin/scenario/r4/scenarioManagerWin.fsp", true, true, true);
    }
    
    public ScenarioManagerController(ScenarioRegistry scenarioRegistry) {
        super();
        this.scenarioRegistry = scenarioRegistry;
    }
    
    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        mv = cboScenarios.getModelAndView(Scenario.class);
        activeScenario = ScenarioContext.getActiveScenario();
        mv.setRenderer(scenarioRenderer);
        mv.setModel(model);
        refreshScenarios();
    }
    
    private void refreshScenarios() {
        cboScenarios.setSelectedItem(null);
        model.clear();
        model.addAll(scenarioRegistry.getAll());
        cboScenarios.setPlaceholder(
            StrUtil.getLabel(model.isEmpty() ? "fcf.scenario.cbox.placeholder.none" : "fcf.scenario.cbox.placeholder"));
        Collections.sort(model, scenarioComparator);
        disableButtons(true, false);
    }
    
    private void rerenderScenarios() {
        activeScenario = ScenarioContext.getActiveScenario();
        mv.rerender();
        disableButtons(getSelectedScenario() == null, false);
    }

    private boolean disableButtons(boolean disable, boolean all) {
        (all ? toolbar : scenarioButtons).disableChildren(disable, true);
        return disable;
    }

    @EventHandler(value = "change", target = "@cboScenarios")
    private void onChange$cboScenarios(Event event) {
        if (event.getData() instanceof Comboitem) {
            cboScenarios.setSelectedItem((Comboitem) event.getData());
        }

        boolean disabled = disableButtons(getSelectedScenario() == null, false);
        
        if (disabled) {
            setMessage(null);
        } else {
            doAction(Action.LOAD);
        }
    }
    
    @EventHandler(value = "click", target = "btnReload")
    private void onClick$btnReload() {
        doAction(Action.RELOAD);
    }
    
    @EventHandler(value = "click", target = "btnDelete")
    private void onClick$btnDelete() {
        DialogUtil.confirm("Delete all resources for this scenario?", getSelectedScenario().getName(), (confirm) -> {
            if (confirm) {
                doAction(Action.DELETE);
            }
        });
    }
    
    @EventHandler(value = "click", target = "btnReset")
    private void onClick$btnReset() {
        DialogUtil.confirm("Reset this scenario to its baseline state?", getSelectedScenario().getName(), (confirm) -> {
            if (confirm) {
                doAction(Action.RESET);
            }
        });
    }
    
    @EventHandler(value = "click", target = "btnDeleteAll")
    private void onClick$btnDeleteAll() {
        DialogUtil.confirm("Delete resources across all scenarios?", "All Scenarios", (confirm) -> {
            if (confirm) {
                doAction(Action.DELETEALL);
            }
        });
    }
    
    @EventHandler(value = "click", target = "btnView")
    private void onClick$btnView() {
        ViewResourcesController.show(getSelectedScenario(), (changed) -> {
            if (changed) {
                doAction(Action.RELOAD);
            }
        });
    }
    
    @EventHandler(value = "click", target = "btnContext")
    private void onClick$btnContext() {
        ScenarioContext.changeScenario(getSelectedScenario());
    }
    
    /**
     * Queues an action to be performed.
     *
     * @param action Action to be performed.
     */
    private void doAction(Action action) {
        Event event = new Event("action", root, action);
        setMessage(null);
        root.addMask(action + "...");
        EventUtil.post(event);
    }
    
    /**
     * Invokes the action specified in the event data.
     *
     * @param event The event containing the action to invoke.
     */
    @EventHandler("action")
    private void onAction(Event event) {
        root.removeMask();
        Scenario scenario = getSelectedScenario();
        Action action = (Action) event.getData();
        setMessage(action.label + "...");
        String result = null;
        disableButtons(true, true);

        if (action == Action.DELETEALL || scenario != null) {
            try {
                
                switch (action) {
                    case LOAD:
                        if (scenario.isLoaded()) {
                            result = "Scenario contains " + scenario.getResourceCount() + " resource(s)";
                            break;
                        }
                        
                        // Fall through intended here.
                        
                    case RELOAD:
                        result = "Loaded " + scenario.load() + " resource(s)";
                        break;
                        
                    case RESET:
                        result = "Created " + scenario.initialize() + " resource(s)";
                        break;
                        
                    case DELETE:
                        result = "Deleted " + scenario.destroy() + " resource(s)";
                        break;
                        
                    case DELETEALL:
                        int count = 0;
                        
                        for (Scenario ascenario : model) {
                            count += ascenario.destroy();
                        }
                        
                        result = "Deleted " + count + " resource(s) across " + model.size() + " scenario(s)";
                }
            } catch (Exception e) {
                result = FCFUtil.formatExceptionForDisplay(e);
            }
        }
        
        setMessage(result);
        disableButtons(false, true);
    }
    
    /**
     * Returns the currently selected scenario, or null if none.
     *
     * @return The currently selected scenario.
     */
    private Scenario getSelectedScenario() {
        Comboitem item = cboScenarios.getSelectedItem();
        return item == null ? null : (Scenario) item.getData();
    }
    
    /**
     * Displays the specified message;
     *
     * @param msg Message to display.
     */
    private void setMessage(String msg) {
        lblMessage.setLabel(msg);
    }
    
    // Scenario context change events
    
    @Override
    public void pending(ISurveyResponse response) {
        response.accept();
    }
    
    @Override
    public void committed() {
        rerenderScenarios();
        
        if (activeScenario == null) {
            setMessage("No scenario is currently active.");
        } else {
            setMessage("Active scenario set to: " + activeScenario.getName());
        }
    }
    
    @Override
    public void canceled() {
    }
}
