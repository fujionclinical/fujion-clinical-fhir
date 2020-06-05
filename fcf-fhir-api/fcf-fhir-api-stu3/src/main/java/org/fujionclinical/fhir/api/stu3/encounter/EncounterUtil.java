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
package org.fujionclinical.fhir.api.stu3.encounter;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import org.fujionclinical.api.encounter.EncounterQueryCriteria;
import org.fujionclinical.api.spring.SpringUtil;
import org.fujionclinical.fhir.api.common.query.IResourceQueryEx;
import org.fujionclinical.fhir.api.stu3.common.ClientUtil;
import org.fujionclinical.fhir.api.stu3.common.FhirUtilStu3;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.*;

/**
 * Encounter-related utility functions.
 */
public class EncounterUtil {

    public static final Coding primaryType = new Coding("http://hl7.org/fhir/v3/ParticipationType", "PPRF",
            "primary performer");

    private static volatile Map<String, CodeableConcept> serviceCategories;

    /**
     * Returns a reference to the encounter search engine.
     *
     * @return Encounter search engine.
     */
    @SuppressWarnings("unchecked")
    public static IResourceQueryEx<Encounter, EncounterQueryCriteria> getSearchEngine() {
        return SpringUtil.getBean("encounterSearchEngine", IResourceQueryEx.class);
    }

    /**
     * Perform a search based on given criteria.
     *
     * @param criteria Search criteria.
     * @return Resources matching the search criteria.
     */
    public static List<Encounter> search(EncounterQueryCriteria criteria) {
        return getSearchEngine().query(criteria);
    }

    /**
     * Returns the default encounter for the current institution for the specified patient. Search
     * is restricted to encounters belonging to the current institution, with care setting codes of
     * 'O', 'E', or 'I'. For inpatient encounters, the discharge date must be null and the admission
     * date must precede the current date (there are anomalous entries where the admission date is
     * in the future). For non-inpatient encounters, the admission date must fall on the same day as
     * the current date. If more than one encounter meets these criteria, further filtering is
     * applied. An encounter whose location matches the current location is selected preferentially.
     * Failing a match on location, non-inpatient encounters are given weight over inpatient
     * encounters. Failing all that, the first matching encounter is returned.
     *
     * @param patient Patient whose default encounter is sought.
     * @return The default encounter or null if one was not found.
     */
    public static Encounter getDefaultEncounter(Patient patient) {
        if (patient == null) {
            return null;
        }

        return null;
    }

    public static Encounter create(
            Patient patient,
            Date date,
            Location location,
            String sc) {
        Encounter encounter = new Encounter();
        Reference pat = new Reference(patient);
        encounter.setSubject(pat);
        Period period = new Period();
        period.setStart(date, TemporalPrecisionEnum.SECOND);
        encounter.setPeriod(period);
        Reference loc = new Reference(location);
        Encounter.EncounterLocationComponent encloc = encounter.addLocation();
        encloc.setPeriod(period);
        encloc.setLocation(loc);
        CodeableConcept type = encounter.addType();
        CodeableConcept cat = getServiceCategory(sc);
        type.setText(cat.getText());
        type.getCoding().addAll(cat.getCoding());
        return encounter;
    }

    public static CodeableConcept createServiceCategory(
            String sc,
            String shortDx,
            String longDx) {
        CodeableConcept cpt = new CodeableConcept();
        cpt.setText(longDx);
        Coding coding = new Coding();
        coding.setCode(sc);
        coding.setDisplay(shortDx);
        cpt.getCoding().add(coding);
        return cpt;
    }

    public static CodeableConcept getServiceCategory(String category) {
        initServiceCategories();

        if (category == null) {
            return null;
        }

        CodeableConcept cat = serviceCategories.get(category);

        if (cat == null) {
            cat = createServiceCategory(category, "Unknown", "Unknown service category");
        }

        return cat;
    }

    public static Collection<CodeableConcept> getServiceCategories() {
        initServiceCategories();
        return serviceCategories.values();
    }

    private static void initServiceCategories() {
        if (serviceCategories == null) {
            loadServiceCategories();
        }
    }

    private static synchronized void loadServiceCategories() {
        if (serviceCategories == null) {
            Map<String, CodeableConcept> map = new LinkedHashMap<>();
            Bundle bundle = ClientUtil.getFhirClient().search().forResource(ValueSet.class)
                    .where(CodeSystem.NAME.matchesExactly().value("EncounterType")).returnBundle(Bundle.class).execute();

            for (CodeSystem cs : FhirUtilStu3.getEntries(bundle, CodeSystem.class)) {
                UriType system = cs.getUrlElement();

                for (ConceptDefinitionComponent concept : cs.getConcept()) {
                    CodeableConcept cc = new CodeableConcept();
                    Coding coding = cc.addCoding();
                    coding.setCode(concept.getCode());
                    coding.setDisplay(concept.getDisplay());
                    coding.setSystemElement(system);
                    cc.setText(concept.getDefinition());
                    map.put(coding.getCode(), cc);
                }
            }

            serviceCategories = map;
        }

        return;
    }

    public static String getServiceCategory(Encounter encounter) {
        CodeableConcept cpt = encounter == null ? null : FhirUtilStu3.getFirst(encounter.getType());
        Coding coding = cpt == null ? null : FhirUtilStu3.getFirst(cpt.getCoding());
        return coding == null ? null : coding.getCode();
    }

    public static boolean isLocked(Encounter encounter) {
        EncounterStatus status = encounter.getStatus();
        return status == EncounterStatus.FINISHED;
    }

    public static boolean isPrepared(Encounter encounter) {
        return encounter != null && !encounter.getLocation().isEmpty() && !encounter.getParticipant().isEmpty()
                && getServiceCategory(encounter) != null;
    }

    public static EncounterParticipantComponent getParticipantByType(
            Encounter encounter,
            Coding participationType) {
        for (EncounterParticipantComponent p : encounter.getParticipant()) {
            if (hasType(p, participationType)) {
                return p;
            }
        }

        return null;
    }

    public static boolean isPrimary(EncounterParticipantComponent participant) {
        return hasType(participant, primaryType);
    }

    public static boolean removeType(
            EncounterParticipantComponent participant,
            Coding participationType) {
        CodeableConcept cpt;
        boolean found = false;

        while ((cpt = findType(participant, participationType)) != null) {
            participant.getType().remove(cpt);
            found = true;
        }

        return found;
    }

    public static boolean addType(
            EncounterParticipantComponent participant,
            Coding participationType) {
        if (!hasType(participant, participationType)) {
            CodeableConcept cpt = participant.addType();
            cpt.getCoding().add(participationType);
            return true;
        }

        return false;
    }

    public static boolean hasType(
            EncounterParticipantComponent participant,
            Coding participationType) {
        return findType(participant, participationType) != null;
    }

    private static CodeableConcept findType(
            EncounterParticipantComponent participant,
            Coding participationType) {
        if (participant != null) {
            for (CodeableConcept tp : participant.getType()) {
                for (Coding coding : tp.getCoding()) {
                    if (coding.getSystem().equals(participationType.getSystem())
                            && coding.getCode().equals(participationType.getCode())) {
                        return tp;
                    }
                }
            }
        }

        return null;
    }

    public static EncounterParticipantComponent getPrimaryParticipant(Encounter encounter) {
        return getParticipantByType(encounter, primaryType);
    }

    public static HumanName getName(EncounterParticipantComponent participant) {
        IBaseResource resource = ClientUtil.getResource(participant.getIndividual());
        List<HumanName> names = FhirUtilStu3.getNames(resource);
        return names == null ? null : FhirUtilStu3.getName(names);
    }

    public static Practitioner getPractitioner(EncounterParticipantComponent participant) {
        if (participant == null) {
            return null;
        }

        Reference resource = participant.getIndividual();
        IBaseResource ele = resource.getResource();
        return ele instanceof Practitioner ? (Practitioner) ele : null;
    }

    public static List<Practitioner> getPractitioners(List<EncounterParticipantComponent> participants) {
        List<Practitioner> list = new ArrayList<>();

        for (EncounterParticipantComponent participant : participants) {
            Practitioner practitioner = getPractitioner(participant);

            if (practitioner != null) {
                list.add(practitioner);
            }
        }
        return list;
    }

    /**
     * Returns an encounter location with the specified physical type.
     *
     * @param encounter    An encounter.
     * @param physicalType The physical location type sought.
     * @return The encounter location corresponding to the specified physical type, or null if none
     *         found.
     */
    public static EncounterLocationComponent getLocationByPhysicalType(
            Encounter encounter,
            String physicalType) {
        for (EncounterLocationComponent encounterLocation : encounter.getLocation()) {
            Location location = ClientUtil.getResource(encounterLocation.getLocation(), Location.class);

            if (physicalType.equals(FhirUtilStu3.getFirst(location.getPhysicalType().getCoding()).getCode())) {
                return encounterLocation;
            }
        }

        return null;
    }

    /**
     * Enforces static class.
     */
    protected EncounterUtil() {
    }

}
