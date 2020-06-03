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
package org.fujionclinical.fhir.plugin.orders.r4;

import org.fujion.common.StrUtil;
import org.fujionclinical.fhir.api.r4.common.FhirUtilR4;
import org.fujionclinical.fhir.lib.sharedforms.r4.controller.ResourceListView;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller for patient orders display.
 */
public class MainController extends ResourceListView<IBaseResource, IBaseResource> {
    
    // @formatter:off
    private static final String QUERY = "Patient?_id=#"
            + "&_revinclude=MedicationRequest:patient"
            + "&_revinclude=ServiceRequest:subject"
            + "&_revinclude=NutritionOrder:patient"
            + "&_revinclude=DeviceRequest:subject";
    // @formatter:on
    
    @Override
    protected void setup() {
        setup(IBaseResource.class, Bundle.class, "Orders", "Order Detail", QUERY, 1, "Type^^min", "Date^^min", "Order^^1", "Notes^^1");
    }
    
    @Override
    protected void createSubscriptions(Class<? extends IBaseResource> clazz) {
        createSubscription(MedicationRequest.class);
        createSubscription(ServiceRequest.class);
        createSubscription(NutritionOrder.class);
        createSubscription(DeviceRequest.class);
    }
    
    @Override
    protected List<IBaseResource> processBundle(Bundle bundle) {
        return FhirUtilR4.getEntries(bundle, null, Collections.singletonList(Patient.class));
    }
    
    @Override
    protected void populate(IBaseResource resource, List<Object> columns) {
        if (resource instanceof ServiceRequest) {
            render((ServiceRequest) resource, columns);
        } else if (resource instanceof NutritionOrder) {
            render((NutritionOrder) resource, columns);
        } else if (resource instanceof MedicationRequest) {
            render((MedicationRequest) resource, columns);
        } else if (resource instanceof DeviceRequest) {
            render((DeviceRequest) resource, columns);
        }
    }
    
    private void render(ServiceRequest request, List<Object> columns) {
        columns.add("Procedure");
        columns.add(request.getAuthoredOn());
        columns.add(request.getCode());
        columns.add(request.getNote());
    }
    
    private void render(NutritionOrder request, List<Object> columns) {
        columns.add("Nutrition");
        columns.add(request.getDateTime());
        columns.add("");
        columns.add("");
    }
    
    private void render(MedicationRequest order, List<Object> columns) {
        columns.add("Medication");
        columns.add(order.getAuthoredOn());
        
        if (order.hasMedicationReference()) {
            Medication medication = getFhirService().getResource((Reference) order.getMedication(), Medication.class);
            columns.add(medication.getCode());
        } else {
            columns.add(order.getMedication());
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (Dosage di : order.getDosageInstruction()) {
            Dosage.DosageDoseAndRateComponent dr = di.getDoseAndRateFirstRep();
            append(sb, dr.getDoseQuantity(), " ");
            append(sb, dr.getRateQuantity(), " ");
            append(sb, di.getSite(), " ");
            append(sb, di.getMethod(), " ");
            append(sb, di.getRoute(), " ");
            append(sb, di.getTiming(), " ");
            append(sb, di.getText(), " ");
            
            Type prn = di.getAsNeeded();
            
            if (prn instanceof BooleanType) {
                if (((BooleanType) prn).getValue()) {
                    append(sb, "PRN", " ");
                }
            } else {
                append(sb, prn, " ");
            }
        }
        
        columns.add(sb);
    }
    
    private void render(DeviceRequest request, List<Object> columns) {
        columns.add("Device Use");
        columns.add(request.getAuthoredOn());
        columns.add(request.getCode());
        columns.add(request.getNote());
    }
    
    private void append(StringBuilder sb, IBaseDatatype value, String delimiter) {
        append(sb, FhirUtilR4.getDisplayValueForType(value), delimiter);
    }
    
    private void append(StringBuilder sb, String value, String delimiter) {
        StrUtil.strAppend(sb, value, delimiter);
    }
    
    @Override
    protected void initModel(List<IBaseResource> orders) {
        model.addAll(orders);
    }
    
}
