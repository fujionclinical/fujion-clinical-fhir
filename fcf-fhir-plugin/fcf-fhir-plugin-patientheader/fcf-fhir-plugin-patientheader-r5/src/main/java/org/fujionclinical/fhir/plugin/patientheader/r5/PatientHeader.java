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
package org.fujionclinical.fhir.plugin.patientheader.r5;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.DateUtil;
import org.fujion.common.StrUtil;
import org.fujion.component.*;
import org.fujionclinical.api.domain.IUser;
import org.fujionclinical.api.event.IGenericEvent;
import org.fujionclinical.api.security.SecurityUtil;
import org.fujionclinical.fhir.lib.patientselection.core.r5.PatientSelection;
import org.fujionclinical.fhir.api.r5.common.FhirUtil;
import org.fujionclinical.fhir.api.r5.patient.PatientContext;
import org.fujionclinical.shell.elements.ElementPlugin;
import org.fujionclinical.shell.plugins.PluginController;
import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.r5.model.Patient.PatientCommunicationComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Controller for patient header plugin.
 */
public class PatientHeader extends PluginController {

    private static final Log log = LogFactory.getLog(PatientHeader.class);

    @WiredComponent
    private Toolbar tbPatient;

    @WiredComponent
    private Button btnDetail;

    @WiredComponent
    private Popup popDetail;

    @WiredComponent(value = "popDetail.pnlDetail")
    private Paneview pnlDetail;

    @WiredComponent
    private Label lblName;

    @WiredComponent
    private Label lblGender;

    @WiredComponent
    private Label lblDOBLabel;

    @WiredComponent
    private Label lblDOB;

    @WiredComponent
    private Label lblDODLabel;

    @WiredComponent
    private Label lblDOD;

    @WiredComponent
    private Label lblUser;

    private String noSelection;

    private Patient patient;

    private String patientName;

    private boolean needsDetail = true;
    private final IGenericEvent<Patient> patientChangeListener = (event, patient) -> setPatient(patient);
    private boolean showUser = true;

    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        noSelection = lblName.getLabel();
        IUser user = SecurityUtil.getAuthenticatedUser();
        lblUser.setVisible(showUser);

        if (user == null) {
            setLabel(lblUser, "Unknown User", null);
        } else {
            setLabel(lblUser, user.getFullName() + " @ " + user.getSecurityDomain().getName(), null);
        }

        setPatient(PatientContext.getActivePatient());
        PatientContext.getPatientContext().addListener(patientChangeListener);
    }

    @Override
    public void onLoad(ElementPlugin plugin) {
        super.onLoad(plugin);
        plugin.registerProperties(this, "showUser");
    }

    @Override
    public void onUnload() {
        PatientContext.getPatientContext().removeListener(patientChangeListener);
    }

    @EventHandler(value = "click", target = "lnkSelect")
    private void onClick$lnkSelect() {
        PatientSelection.show(true);
    }

    @EventHandler(value = "click", target = "@btnDetail")
    private void onClick$btnDetail() {
        buildDetail();
        popDetail.open(btnDetail, "left top", "right bottom");
    }

    private void setPatient(Patient patient) {
        this.patient = patient;
        hideLabels();
        needsDetail = true;
        pnlDetail.destroyChildren();

        if (log.isDebugEnabled()) {
            log.debug("patient: " + patient);
        }

        if (patient == null) {
            lblName.setLabel(noSelection);
            btnDetail.setDisabled(true);
            return;
        }

        btnDetail.setDisabled(false);
        patientName = FhirUtil.formatName(patient.getName());
        String mrn = FhirUtil.getMRNString(patient);
        lblName.setLabel(patientName + (mrn.isEmpty() ? "" : "  (" + mrn + ")"));
        setLabel(lblDOB, formatDateAndAge(patient.getBirthDate()), lblDOBLabel);
        setLabel(lblDOD, formatDOD(patient.getDeceased()), lblDODLabel);
        setLabel(lblGender, patient.hasGender() ? patient.getGender().getDisplay() : null, null);
    }

    private String formatDOD(DataType value) {
        if (value == null) {
            return null;
        }

        DateType dod = FhirUtil.getTyped(value, DateType.class);

        if (dod != null) {
            return formatDateAndAge(dod.getValue());
        }

        BooleanType isDead = FhirUtil.getTyped(value, BooleanType.class);

        if (isDead != null && isDead.getValue()) {
            return "unknown";
        }

        return null;
    }

    private String formatDateAndAge(Date date) {
        return date == null ? null : DateUtil.formatDate(date) + " (" + DateUtil.formatAge(date) + ")";
    }

    private void setLabel(
            Label label,
            String value,
            BaseUIComponent associatedComponent) {
        label.setLabel(value);
        label.setVisible(value != null && !value.isEmpty());

        if (associatedComponent != null) {
            associatedComponent.setVisible(label.isVisible());
        }
    }

    private void hideLabels() {
        for (Label child : tbPatient.getChildren(Label.class)) {
            if (child != lblName) {
                child.setVisible(false);
            }
        }
    }

    private boolean buildDetail() {
        if (!needsDetail) {
            return false;
        }

        needsDetail = false;

        Pane header = null;

        // Names

        for (HumanName name : patient.getName()) {

            String nm = FhirUtil.formatName(name);

            if (patientName.equals(nm)) {
                continue;
            }

            if (header == null) {
                header = addHeader("Other Names");
            }

            addDetail(header, nm, null);
        }

        // Identifiers

        header = null;

        for (Identifier id : patient.getIdentifier()) {
            if (header == null) {
                header = addHeader("Identifiers");
            }

            String use = id.hasUse() ? id.getUse().getDisplay() : "";
            String system = id.hasSystem() ? id.getSystem() : "";
            String value = id.hasValue() ? id.getValue() : "";

            if (!StringUtils.isEmpty(system)) {
                value += " (" + system + ")";
            }

            addDetail(header, value, use);
        }

        // Communication

        header = null;

        for (PatientCommunicationComponent comm : patient.getCommunication()) {
            if (header == null) {
                header = addHeader("Communication");
            }

            String language = FhirUtil.getDisplayValueForType(comm.getLanguage());

            if (comm.getPreferred()) {
                language += " (preferred)";
            }

            addDetail(header, language, null);
        }
        // Telecom info

        header = null;

        List<ContactPoint> telecoms = new ArrayList<>(patient.getTelecom());
        Collections.sort(telecoms, (cp1, cp2) -> cp1.getRank() - cp2.getRank());

        for (ContactPoint telecom : telecoms) {
            if (header == null) {
                header = addHeader("Contact Details");
            }

            String type = telecom.hasSystem() ? telecom.getSystem().getDisplay() : "";
            String use = telecom.hasUse() ? telecom.getUse().getDisplay() : "";

            if (!StringUtils.isEmpty(use)) {
                type += " (" + use + ")";
            }

            addDetail(header, telecom.getValue(), type);
        }

        // Address(es)
        header = null;

        for (Address address : patient.getAddress()) {
            if (header == null) {
                header = addHeader("Addresses");
            }

            String type = address.hasType() ? address.getType().getDisplay() : "";
            String use = address.hasUse() ? address.getUse().getDisplay() : "";

            if (!StringUtils.isEmpty(type)) {
                use += " (" + type + ")";
            }

            addDetail(header, " ", use);

            for (StringType line : address.getLine()) {
                addDetail(header, line.getValue(), null);
            }

            StringBuilder line = new StringBuilder();
            line.append(address.getCity()).append(", ");
            line.append(address.getState()).append("  ");
            line.append(address.getPostalCode());
            addDetail(header, line.toString(), null);
        }

        if (pnlDetail.getFirstChild() == null) {
            addHeader(StrUtil.getLabel("fcfpatientheader.nodetail.label"));
        }

        return true;
    }

    private Pane addHeader(String text) {
        Pane header = new Pane();
        header.setTitle(text);
        pnlDetail.addChild(header);
        return header;
    }

    private void addDetail(
            Pane header,
            String text,
            String label) {
        if (StringUtils.isEmpty(text)) {
            return;
        }

        if (label != null) {
            Label lbl = new Label(label);
            lbl.addClass("fcf-patientheader-label");
            header.addChild(lbl);
        }

        header.addChild(new Label(text));
    }

    public boolean getShowUser() {
        return showUser;
    }

    public void setShowUser(boolean showUser) {
        this.showUser = showUser;

        if (lblUser != null) {
            lblUser.setVisible(showUser);
        }
    }

}
