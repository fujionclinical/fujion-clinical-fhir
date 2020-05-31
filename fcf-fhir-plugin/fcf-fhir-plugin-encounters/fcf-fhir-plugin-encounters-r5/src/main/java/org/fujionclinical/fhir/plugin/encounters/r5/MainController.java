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
package org.fujionclinical.fhir.plugin.encounters.r5;

import org.fujion.annotation.WiredComponent;
import org.fujion.component.*;
import org.fujion.event.DblclickEvent;
import org.fujionclinical.api.encounter.EncounterContext;
import org.fujionclinical.api.encounter.IEncounter;
import org.fujionclinical.api.event.IEventSubscriber;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.r5.common.ClientUtil;
import org.fujionclinical.fhir.api.r5.encounter.EncounterWrapper;
import org.fujionclinical.fhir.lib.sharedforms.r5.controller.ResourceListView;
import org.fujionclinical.shell.elements.ElementPlugin;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.HumanName;
import org.hl7.fhir.r5.model.Location;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller for patient encounters display.
 */
public class MainController extends ResourceListView<Encounter, Encounter> {

    private IEncounter lastEncounter;

    @WiredComponent
    private Rows rows;

    private final IEventSubscriber<IEncounter> encounterChangeListener = (eventName, encounter) -> setEncounter(encounter);

    @WiredComponent
    private Columns columns;

    @Override
    protected void setup() {
        setup(Encounter.class, Bundle.class, "Encounters", "Encounter Detail", "Encounter?patient=#", 1, "", "Date", "EncounterStatus", "Location", "Providers");
        columns.getFirstChild(Column.class).setStyles("width: 1%; min-width: 40px");
    }

    @Override
    protected void render(
            Encounter encounter,
            List<Object> columns) {
        columns.add(" ");
        columns.add(encounter.getPeriod());
        columns.add(encounter.getStatus());
        columns.add(getLocations(encounter));
        columns.add(getParticipants(encounter));
    }

    private List<Location> getLocations(Encounter encounter) {
        List<Encounter.EncounterLocationComponent> locations = encounter.getLocation();

        return locations.isEmpty() ? null : locations.stream()
                .map(encloc -> ClientUtil.getResource(encloc.getLocation(), Location.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<HumanName> getParticipants(Encounter encounter) {
        List<Encounter.EncounterParticipantComponent> participants = encounter.getParticipant();

        return participants.isEmpty() ? null : participants.stream()
                .map(encpart -> ClientUtil.getResource(encpart.getIndividual()))
                .map(individual -> FhirUtil.getProperty(individual, "getName", HumanName.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    protected void initModel(List<Encounter> entries) {
        model.addAll(entries);
    }

    @Override
    protected void renderRow(
            Row row,
            Encounter encounter) {
        super.renderRow(row, encounter);

        row.addEventListener(DblclickEvent.class, (event) -> 
                EncounterContext.changeEncounter(EncounterWrapper.wrap(encounter)));
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

    private void setEncounter(IEncounter encounter) {
        updateRowStatus(lastEncounter, false);
        updateRowStatus(encounter, true);
        lastEncounter = encounter;
    }

    private void updateRowStatus(
            IEncounter encounter,
            boolean activeContext) {
        Row row = encounter == null ? null : (Row) rows.findChild(child -> {
            Encounter enc = (Encounter) child.getData();
            return encounter.getId().equals(enc.getIdElement().getIdPart());
        });

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
