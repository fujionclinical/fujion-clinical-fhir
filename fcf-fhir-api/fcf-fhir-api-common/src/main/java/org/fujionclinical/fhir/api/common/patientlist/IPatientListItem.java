package org.fujionclinical.fhir.api.common.patientlist;

public interface IPatientListItem extends Comparable<IPatientListItem> {
    IPatientAdapter getPatient();

    String getInfo();

    @Override
    default int compareTo(IPatientListItem listItem) {
        return getPatient().compareTo(listItem.getPatient());
    }

    default boolean equalTo(Object object) {
        return object instanceof IPatientListItem && getPatient().equalTo(((IPatientListItem) object).getPatient());
    }
}
