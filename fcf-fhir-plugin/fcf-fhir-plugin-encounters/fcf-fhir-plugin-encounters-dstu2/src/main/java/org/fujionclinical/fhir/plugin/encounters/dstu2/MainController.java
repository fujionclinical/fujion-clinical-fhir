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
package org.fujionclinical.fhir.plugin.encounters.dstu2;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.*;
import org.fujion.event.DblclickEvent;
import org.fujionclinical.api.event.IGenericEvent;
import org.fujionclinical.fhir.dstu2.api.common.ClientUtil;
import org.fujionclinical.fhir.dstu2.api.common.FhirUtil;
import org.fujionclinical.fhir.dstu2.api.encounter.EncounterContext;
import org.fujionclinical.fhir.lib.sharedforms.dstu2.controller.ResourceListView;
import org.fujionclinical.shell.elements.ElementPlugin;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for patient encounters display.
 */
public class MainController extends ResourceListView<Encounter, Encounter> {

    private Encounter lastEncounter;

    @WiredComponent
    private Rows rows;

    @WiredComponent
    private Columns columns;

    private final IGenericEvent<Encounter> encounterChangeListener = (eventName, encounter) -> setEncounter(encounter);

    @Override
    protected void setup() {
        setup(Encounter.class, "Encounters", "Encounter Detail", "Encounter?patient=#", 1, "", "Date", "Status", "Location", "Providers");
        columns.getFirstChild(Column.class).setStyles("width: 1%; min-width: 40px");
    }
    
    @Override
    protected void render(Encounter encounter, List<Object> columns) {
        columns.add(" ");
        columns.add(encounter.getPeriod());
        columns.add(encounter.getStatus());
        columns.add(getLocations(encounter));
        columns.add(getParticipants(encounter));
    }

    private List<Location> getLocations(Encounter encounter) {
        List<Encounter.Location> locations = encounter.getLocation();

        return locations.isEmpty() ? null : locations.stream()
                .map(encloc -> ClientUtil.getResource(encloc.getLocation(), Location.class))
                .filter(location -> location != null)
                .collect(Collectors.toList());
    }

    private List<HumanNameDt> getParticipants(Encounter encounter) {
        List<Encounter.Participant> participants = encounter.getParticipant();

        return participants.isEmpty() ? null : participants.stream()
                .map(encpart -> ClientUtil.getResource(encpart.getIndividual()))
                .map(individual -> FhirUtil.getProperty(individual, "getName", HumanNameDt.class))
                .filter(name -> name != null)
                .collect(Collectors.toList());
    }

    @Override
    protected void initModel(List<Encounter> entries) {
        model.addAll(entries);
    }

    @Override
    protected void renderRow(Row row, Encounter encounter) {
        super.renderRow(row, encounter);

        row.addEventListener(DblclickEvent.class, (event) -> {
            EncounterContext.changeEncounter(encounter);
        });
    }

    @Override
    public void onLoad(ElementPlugin plugin) {
        super.onLoad(plugin);
        EncounterContext.getEncounterContext().addListener(encounterChangeListener);
        lastEncounter = EncounterContext.getActiveEncounter();
    }

    @Override
    public void onUnload() {
        super.onUnload();
        EncounterContext.getEncounterContext().removeListener(encounterChangeListener);
    }

    private void setEncounter(Encounter encounter) {
        updateRowStatus(lastEncounter, false);
        updateRowStatus(encounter, true);
        lastEncounter = encounter;
    }

    private void updateRowStatus(Encounter encounter, boolean activeContext) {
        Row row = encounter == null ? null : (Row) rows.findChildByData(encounter);

        if (row != null) {
            Rowcell cell = row.getFirstChild(Rowcell.class);
            BaseUIComponent flag;

            if (cell.hasChildren()) {
                flag = (BaseUIComponent) cell.getFirstChild();
            } else {
                cell.addChild(flag = new Div());
            }

            flag.setClasses(activeContext ? "fa fa-check" : "-fa -fa-check");
        }
    }
}
