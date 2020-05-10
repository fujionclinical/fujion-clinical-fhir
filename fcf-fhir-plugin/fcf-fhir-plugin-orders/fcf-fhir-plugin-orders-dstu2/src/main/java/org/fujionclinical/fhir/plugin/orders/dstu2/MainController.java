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
package org.fujionclinical.fhir.plugin.orders.dstu2;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.primitive.BooleanDt;
import org.fujion.common.StrUtil;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtil;
import org.fujionclinical.fhir.lib.sharedforms.dstu2.controller.ResourceListView;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.Collections;
import java.util.List;

/**
 * Controller for patient orders display.
 */
public class MainController extends ResourceListView<IBaseResource, IBaseResource> {
    
    // @formatter:off
    private static final String QUERY = "Patient?_id=#"
            + "&_revinclude=MedicationOrder:patient"
            + "&_revinclude=ProcedureRequest:subject"
            + "&_revinclude=NutritionOrder:patient"
            + "&_revinclude=DeviceUseRequest:subject";
    // @formatter:on
    
    @Override
    protected void setup() {
        setup(IBaseResource.class, "Orders", "Order Detail", QUERY, 1, "Type^^min", "Date^^min", "Order^^1", "Notes^^1");
    }
    
    @Override
    protected void createSubscriptions(Class<? extends IBaseResource> clazz) {
        createSubscription(MedicationOrder.class);
        createSubscription(ProcedureRequest.class);
        createSubscription(NutritionOrder.class);
        createSubscription(DeviceUseRequest.class);
    }
    
    @Override
    protected List<IBaseResource> processBundle(Bundle bundle) {
        return FhirUtil.getEntries(bundle, null, Collections.singletonList(Patient.class));
    }
    
    @Override
    protected void render(IBaseResource resource, List<Object> columns) {
        if (resource instanceof ProcedureRequest) {
            render((ProcedureRequest) resource, columns);
        } else if (resource instanceof NutritionOrder) {
            render((NutritionOrder) resource, columns);
        } else if (resource instanceof MedicationOrder) {
            render((MedicationOrder) resource, columns);
        } else if (resource instanceof DeviceUseRequest) {
            render((DeviceUseRequest) resource, columns);
        }
    }
    
    private void render(ProcedureRequest request, List<Object> columns) {
        columns.add("Procedure");
        columns.add(request.getOrderedOn());
        columns.add(request.getCode());
        columns.add(request.getNotes());
    }
    
    private void render(NutritionOrder request, List<Object> columns) {
        columns.add("Nutrition");
        columns.add(request.getDateTime());
        columns.add("");
        columns.add("");
    }
    
    private void render(MedicationOrder order, List<Object> columns) {
        columns.add("Medication");
        columns.add(order.getDateWritten());
        
        if (!order.getMedication().isEmpty()) {
            Medication medication = getFhirService().getResource((ResourceReferenceDt) order.getMedication(), Medication.class);
            columns.add(medication.getCode());
        } else {
            columns.add(order.getMedication());
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (MedicationOrder.DosageInstruction di : order.getDosageInstruction()) {
            append(sb, di.getDose(), " ");
            append(sb, di.getRate(), " ");
            append(sb, di.getSite(), " ");
            append(sb, di.getMethod(), " ");
            append(sb, di.getRoute(), " ");
            append(sb, di.getTiming(), " ");
            append(sb, di.getText(), " ");
            
            IDatatype prn = di.getAsNeeded();
            
            if (prn instanceof BooleanDt) {
                if (((BooleanDt) prn).getValue()) {
                    append(sb, "PRN", " ");
                }
            } else {
                append(sb, prn, " ");
            }
        }
        
        columns.add(sb);
    }
    
    private void render(DeviceUseRequest request, List<Object> columns) {
        columns.add("Device Use");
        columns.add(request.getOrderedOn());
        columns.add(request.getDevice().getDisplay().getValue());
        columns.add(request.getNotes());
    }
    
    private void append(StringBuilder sb, IBaseDatatype value, String delimiter) {
        append(sb, FhirUtil.getDisplayValueForType(value), delimiter);
    }
    
    private void append(StringBuilder sb, String value, String delimiter) {
        StrUtil.strAppend(sb, value, delimiter);
    }
    
    @Override
    protected void initModel(List<IBaseResource> orders) {
        model.addAll(orders);
    }
    
}
