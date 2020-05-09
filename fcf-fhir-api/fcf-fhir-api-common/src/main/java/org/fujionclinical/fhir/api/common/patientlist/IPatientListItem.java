package org.fujionclinical.fhir.api.common.patientlist;

import org.hl7.fhir.instance.model.api.IBaseResource;

public interface IPatientListItem<PATIENT extends IBaseResource> extends Comparable<IPatientListItem<PATIENT>> {
    PATIENT getPatient();

    String getInfo();

    void select();

    @Override
    boolean equals(Object object);
}
