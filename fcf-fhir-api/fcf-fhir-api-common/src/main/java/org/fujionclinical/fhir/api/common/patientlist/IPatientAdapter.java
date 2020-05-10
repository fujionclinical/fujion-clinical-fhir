package org.fujionclinical.fhir.api.common.patientlist;

import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.Date;

public interface IPatientAdapter extends Comparable<IPatientAdapter> {

    /**
     * @return The patient's full name suitable for alphabetical sorting.
     */
    String getName();

    /**
     * @return The patient's date of birth.
     */
    Date getDOB();

    /**
     * @return The patient's medical record number.
     */
    String getMRN();

    /**
     * @return The underlying FHIR Patient resource.
     */
    IBaseResource getResource();

    /**
     * Sets the shared patient context to this patient.
     */
    void select();

    default int compareTo(IPatientAdapter patient) {
        String name1 = getName();
        String name2 = patient.getName();
        return name1.compareToIgnoreCase(name2);
    }

    default boolean equalTo(IPatientAdapter patient) {
        return patient != null && FhirUtil.areEqual(patient.getResource(), getResource());
    }
}
