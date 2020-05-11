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
package org.fujionclinical.fhir.lib.patientselection.r4;

import org.apache.commons.lang.StringUtils;
import org.fujion.common.DateUtil;
import org.fujion.common.StrUtil;
import org.fujion.component.BaseUIComponent;
import org.fujion.component.Div;
import org.fujion.component.Image;
import org.fujion.component.Label;
import org.fujionclinical.fhir.lib.patientselection.common.Constants;
import org.fujionclinical.fhir.api.r4.common.FhirUtil;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Address.AddressUse;

import java.util.Date;
import java.util.List;

/**
 * Default class for rendering detail view of patient in patient selection dialog. This class may be
 * overridden to provide an alternate detail view.
 */
public class PatientDetailRenderer implements IPatientDetailRenderer {
    
    /**
     * Render detail view for the specified patient.
     *
     * @param patient Patient whose detail view is to be rendered.
     */
    @Override
    public BaseUIComponent render(Patient patient) {
        BaseUIComponent root = new Div();
        root.addClass("fujion-layout-vertical text-center");
        root.addStyle("align-items", "center");
        
        if (confirmAccess(patient, root)) {
            renderDemographics(patient, root);
        }
        
        return root;
    }
    
    protected void renderDemographics(Patient patient, BaseUIComponent root) {
        root.addChild(new Div());
        Image photo = new Image();
        photo.setStyles("max-height:300px;max-width:300px;padding-bottom:10px");
        photo.setSrc(FhirUtil.getImage(patient.getPhoto(), Constants.SILHOUETTE_IMAGE).getSrc());
        root.addChild(photo);
        addDemographic(root, null, FhirUtil.formatName(patient.getName()), "font-weight: bold");
        addDemographic(root, "mrn", FhirUtil.getMRNString(patient));
        addDemographic(root, "gender", patient.getGender());
        //addDemographic(root, "race", org.springframework.util.StringUtils.collectionToCommaDelimitedString(patient.getRace()));
        addDemographic(root, "age", DateUtil.formatAge(patient.getBirthDate()));
        addDemographic(root, "dob", patient.getBirthDate());
        addDemographic(root, "dod", patient.getDeceased());
        //addDemographic(root, "mother", patient.getMothersFirstName());
        addDemographic(root, "language", patient.getLanguage());
        addContact(root, patient.getTelecom(), "home:phone", null);
        addContact(root, patient.getTelecom(), "home:email", null);
        addContact(root, patient.getTelecom(), "home:fax", "home fax");
        addContact(root, patient.getTelecom(), "work:phone", null);
        addContact(root, patient.getTelecom(), "work:email", null);
        addContact(root, patient.getTelecom(), "work:fax", "work fax");
        
        Address address = FhirUtil.getAddress(patient.getAddress(), AddressUse.HOME);
        
        if (address != null) {
            root.addChild(new Div());
            
            for (StringType line : address.getLine()) {
                addDemographic(root, null, line.getValue());
            }
            
            String city = StringUtils.defaultString(address.getCity());
            String state = StringUtils.defaultString(address.getState());
            String zip = StringUtils.defaultString(address.getPostalCode());
            String sep = city.isEmpty() || state.isEmpty() ? "" : ", ";
            addDemographic(root, null, city + sep + state + "  " + zip);
        }
        
    }
    
    /**
     * Confirm access to patient.
     *
     * @param patient The patient to check.
     * @param root The root component.
     * @return True if access confirmed.
     */
    private boolean confirmAccess(Patient patient, BaseUIComponent root) {
        boolean allowed = confirmAccess(patient);
        
        if (!allowed) {
            addDemographic(root, null, getDemographicLabel("restricted"), "font-weight: bold");
        }
        
        return allowed;
    }
    
    /**
     * Override to restrict access to certain patients.
     *
     * @param patient The patient to check.
     * @return True if access confirmed.
     */
    protected boolean confirmAccess(Patient patient) {
        return true; //!patient.isRestricted();
    }
    
    /**
     * Adds a contact element to the demographic panel. Uses default styling.
     *
     * @param root Root component.
     * @param contacts List of contacts from which to select.
     * @param type Type of contact desired (e.g., "home:phone").
     * @param labelId The id of the label to use.
     */
    protected void addContact(BaseUIComponent root, List<ContactPoint> contacts, String type, String labelId) {
        ContactPoint contact = FhirUtil.getContact(contacts, type);
        
        if (contact != null) {
            addDemographic(root, labelId == null ? contact.getUse().toCode() : labelId, contact.getValue(), null);
        }
    }
    
    /**
     * Adds a demographic element to the demographic panel. Uses default styling.
     *
     * @param root Root component.
     * @param labelId The id of the label to use.
     * @param object The element to be added.
     */
    protected void addDemographic(BaseUIComponent root, String labelId, Object object) {
        addDemographic(root, labelId, object, null);
    }
    
    /**
     * Adds a demographic element to the demographic panel.
     *
     * @param root Root component.
     * @param labelId The id of the label to use.
     * @param object The element to be added.
     * @param style CSS styling to apply to element (may be null).
     */
    protected void addDemographic(BaseUIComponent root, String labelId, Object object, String style) {
        object = object instanceof PrimitiveType ? ((PrimitiveType<?>) object).getValue() : object;
        String value = object == null ? null
                : object instanceof Date ? DateUtil.formatDate((Date) object) : object.toString().trim();
                
                if (!StringUtils.isEmpty(value)) {
                    Label lbl = new Label((labelId == null ? "" : getDemographicLabel(labelId) + ": ") + value);
                    root.addChild(lbl);
                    
                    if (style != null) {
                        lbl.addStyles(style);
                    }
                }
                
    }
    
    /**
     * Returns the text for the specified label id.
     *
     * @param labelId The id of the label value to locate. If no prefix is present, the id is
     *            prefixed with "patient.selection.demographic.label." to find the associated value.
     * @return Label text.
     */
    protected String getDemographicLabel(String labelId) {
        return StrUtil.getLabel(labelId.contains(".") ? labelId : "patientselection.demographic.label." + labelId);
    }
    
}
