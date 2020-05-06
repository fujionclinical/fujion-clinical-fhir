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
package org.fujionclinical.fhir.plugin.patientphoto.r5;

import org.fujion.annotation.WiredComponent;
import org.fujion.common.StrUtil;
import org.fujion.component.BaseComponent;
import org.fujion.component.Image;
import org.fujion.component.Label;
import org.fujion.component.Popup;
import org.fujionclinical.api.context.ISurveyResponse;
import org.fujionclinical.fhir.lib.patientselection.common.Constants;
import org.fujionclinical.fhir.r5.api.common.FhirUtil;
import org.fujionclinical.fhir.r5.api.patient.PatientContext;
import org.fujionclinical.ui.controller.FrameworkController;
import org.hl7.fhir.r5.model.Patient;

/**
 * Controller for patient photo plugin.
 */
public class PatientPhoto extends FrameworkController implements PatientContext.IPatientContextEvent {

    @WiredComponent
    private Image imgPhoto;

    @WiredComponent("popup.imgFullPhoto")
    private Image imgFullPhoto;

    @WiredComponent
    private Popup popup;

    @WiredComponent("popup.lblCaption")
    private Label lblCaption;

    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        committed();
    }

    @Override
    public void canceled() {
    }

    @Override
    public void committed() {
        Patient patient = PatientContext.getActivePatient();
        Image image = patient == null ? null : FhirUtil.getImage(patient.getPhoto());

        if (patient == null) {
            imgPhoto.setSrc(Constants.NOPATIENT_IMAGE);
            imgPhoto.setPopup(null);
            imgPhoto.setHint(StrUtil.getLabel("patientphoto.no.patient"));
        } else if (image == null) {
            imgPhoto.setSrc(Constants.SILHOUETTE_IMAGE);
            imgPhoto.setPopup(null);
            imgPhoto.setHint(StrUtil.getLabel("patientphoto.no.photo"));
        } else {
            imgPhoto.setSrc(image.getSrc());
            imgPhoto.setHint(null);
            imgPhoto.setPopup(popup);
            imgFullPhoto.setSrc(image.getSrc());
            lblCaption.setLabel(
                patient == null ? "" : FhirUtil.formatName(patient.getName()));
        }

    }

    @Override
    public void pending(ISurveyResponse response) {
        response.accept();
    }

}
