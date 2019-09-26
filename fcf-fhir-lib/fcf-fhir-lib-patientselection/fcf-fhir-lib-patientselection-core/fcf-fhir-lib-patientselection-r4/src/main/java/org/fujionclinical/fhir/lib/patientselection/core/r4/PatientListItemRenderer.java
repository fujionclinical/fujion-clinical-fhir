/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2019 fujionclinical.org
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
package org.fujionclinical.fhir.lib.patientselection.core.r4;

import org.apache.commons.lang.StringUtils;
import org.fujion.common.DateUtil;
import org.fujion.common.StrUtil;
import org.fujion.component.Cell;
import org.fujion.component.Columns;
import org.fujion.component.Grid;
import org.fujion.component.Row;
import org.fujion.event.ChangeEvent;
import org.fujion.model.IComponentRenderer;
import org.fujionclinical.fhir.r4.api.common.FhirUtil;
import org.fujionclinical.fhir.r4.api.patientlist.PatientListItem;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

import java.util.Date;

/**
 * Renderer for patient list items.
 */
public class PatientListItemRenderer implements IComponentRenderer<Row, Object> {

    private final Grid grid;

    /**
     * Force singleton usage.
     *
     * @param grid The grid component.
     */
    public PatientListItemRenderer(Grid grid) {
        this.grid = grid;
    }

    /**
     * Render a list item.
     *
     * @param object The associated PatientListItem or Patient object.
     */
    @Override
    public Row render(Object object) {
        PatientListItem patientListItem;

        if (object instanceof PatientListItem) {
            patientListItem = (PatientListItem) object;
        } else if (object instanceof Patient) {
            patientListItem = new PatientListItem((Patient) object, null);
        } else {
            throw new IllegalArgumentException("Invalid object type: " + object);
        }

        Row row = new Row();
        row.addEventForward(ChangeEvent.TYPE, grid, null);
        row.setData(patientListItem);
        Patient patient = patientListItem.getPatient();
        // If columns are defined, limit rendering to that number of cells.
        Columns columns = grid.getColumns();
        int max = columns == null ? 0 : columns.getChildCount();
        String info = patientListItem.getInfo();

        if (patient != null) {
            HumanName name = FhirUtil.getName(patient.getName());

            if (name == null) {
                name = FhirUtil.parseName(StrUtil.getLabel("patientselection.warn.unknown.patient"));
            }

            addCell(row, name.getFamily(), max);
            addCell(row, StringUtils.join(name.getGiven(), " "), max);
            addCell(row, FhirUtil.getMRNString(patient), max);

            if (StringUtils.isEmpty(info)) {
                Date dob = patient.getBirthDate();
                info = dob == null ? "" : DateUtil.formatDate(dob);
            }
        }

        addCell(row, info, max);
        return row;
    }

    /**
     * Add a cell to the row.
     *
     * @param row Grid row to receive new cell;
     * @param label Text label for the cell.
     * @param max Maximum # of allowable cells.
     * @return True if a cell was added.
     */
    private boolean addCell(Row row, String label, int max) {
        if (max == 0 || row.getChildCount() < max) {
            row.addChild(new Cell(label));
            return true;
        }

        return false;
    }
}
