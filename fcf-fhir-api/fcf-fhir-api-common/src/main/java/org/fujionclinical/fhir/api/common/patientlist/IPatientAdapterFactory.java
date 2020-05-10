package org.fujionclinical.fhir.api.common.patientlist;

import org.fujionclinical.api.event.IGenericEvent;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.*;
import java.util.function.Consumer;

public interface IPatientAdapterFactory {

    /**
     * Fetches and wraps a FHIR Patient resource using specified logical id.
     *
     * @param logicalId The logical id.
     * @return The wrapped Patient resource.
     */
    IPatientAdapter create(String logicalId);

    /**
     * Wraps a FHIR Patient resource.
     *
     * @param resource The FHIR Patient resource.
     * @return The wrapped Patient resource.
     */
    IPatientAdapter create(IBaseResource resource);

    default List<IPatientAdapter> createList(String... logicalIds) {
        return createList(Arrays.asList(logicalIds));
    }

    default List<IPatientAdapter> createList(Collection<String> logicalIds) {
        List<IPatientAdapter> list = new ArrayList<>();

        for (String logicalId : logicalIds) {
            list.add(create(logicalId));
        }

        return list;
    }

    /**
     * Notify consumer when the patient context is changed.
     *
     * @param consumer Consumer of patient context changes.
     */
    void registerListener(IPatientAdapterConsumer consumer);
}
