package org.fujionclinical.fhir.lib.patientselection.v1.common;

import org.fujion.component.Window;
import org.fujion.page.PageUtil;
import org.fujionclinical.fhir.lib.patientselection.common.Constants;

public class PatientSelectionUtil {

    /**
     * Creates an instance of the patient selection dialog.
     *
     * @return The patient selection dialog.
     */
    public static Window createSelectionDialog() {
        return (Window) PageUtil.createPage(Constants.RESOURCE_PATH + "v1/patientSelection.fsp", null).get(0);
    }

    private PatientSelectionUtil() {}
}
