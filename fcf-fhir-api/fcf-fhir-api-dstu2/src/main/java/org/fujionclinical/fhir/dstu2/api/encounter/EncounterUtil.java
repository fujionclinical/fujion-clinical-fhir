/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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
package org.fujionclinical.fhir.dstu2.api.encounter;

import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.BoundCodeDt;
import ca.uhn.fhir.model.primitive.UriDt;
import org.fujionclinical.api.spring.SpringUtil;
import org.fujionclinical.fhir.api.common.query.IResourceQueryEx;
import org.fujionclinical.fhir.dstu2.api.common.ClientUtil;
import org.fujionclinical.fhir.dstu2.api.common.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.*;

/**
 * Encounter-related utility functions.
 */
public class EncounterUtil {

    public static final CodingDt primaryType = new CodingDt("http://hl7.org/fhir/v3/ParticipationType", "PPRF")
            .setDisplay("primary performer");

    private static volatile Map<String, CodeableConceptDt> serviceCategories;

    /**
     * Returns a reference to the encounter search engine.
     *
     * @return Encounter search engine.
     */
    @SuppressWarnings("unchecked")
    public static IResourceQueryEx<Encounter, EncounterSearchCriteria> getSearchEngine() {
        return SpringUtil.getBean("encounterSearchEngine", IResourceQueryEx.class);
    }

    /**
     * Perform a search based on given criteria.
     *
     * @param criteria Search criteria.
     * @return Resources matching the search criteria.
     */
    public static List<Encounter> search(EncounterSearchCriteria criteria) {
        return getSearchEngine().search(criteria);
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

    public static Encounter create(Patient patient, Date date, Location location, String sc) {
        Encounter encounter = new Encounter();
        ResourceReferenceDt pat = new ResourceReferenceDt(patient);
        encounter.setPatient(pat);
        PeriodDt period = new PeriodDt();
        period.setStartWithSecondsPrecision(date);
        encounter.setPeriod(period);
        ResourceReferenceDt loc = new ResourceReferenceDt(location);
        Encounter.Location encloc = encounter.addLocation();
        encloc.setPeriod(period);
        encloc.setLocation(loc);
        CodeableConceptDt type = encounter.addType();
        CodeableConceptDt cat = getServiceCategory(sc);
        type.setText(cat.getText());
        type.getCoding().addAll(cat.getCoding());
        return encounter;
    }

    public static CodeableConceptDt createServiceCategory(String sc, String shortDx, String longDx) {
        CodeableConceptDt cpt = new CodeableConceptDt();
        cpt.setText(longDx);
        CodingDt coding = new CodingDt();
        coding.setCode(sc);
        coding.setDisplay(shortDx);
        cpt.getCoding().add(coding);
        return cpt;
    }

    public static CodeableConceptDt getServiceCategory(String category) {
        initServiceCategories();

        if (category == null) {
            return null;
        }

        CodeableConceptDt cat = serviceCategories.get(category);

        if (cat == null) {
            cat = createServiceCategory(category, "Unknown", "Unknown service category");
        }

        return cat;
    }

    public static Collection<CodeableConceptDt> getServiceCategories() {
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
            Map<String, CodeableConceptDt> map = new LinkedHashMap<>();
            Bundle bundle = ClientUtil.getFhirClient().search().forResource(ValueSet.class)
                    .where(ValueSet.NAME.matchesExactly().value("EncounterType")).returnBundle(Bundle.class).execute();

            for (ValueSet vs : FhirUtil.getEntries(bundle, ValueSet.class)) {
                UriDt system = vs.getCodeSystem().getSystemElement();

                for (ValueSet.CodeSystemConcept concept : vs.getCodeSystem().getConcept()) {
                    CodeableConceptDt cc = new CodeableConceptDt();
                    CodingDt coding = cc.addCoding();
                    coding.setCode(concept.getCode());
                    coding.setDisplay(concept.getDisplay());
                    coding.setSystem(system);
                    cc.setText(concept.getDefinition());
                    map.put(coding.getCode(), cc);
                }
            }

            serviceCategories = map;
        }

        return;
    }

    public static String getServiceCategory(Encounter encounter) {
        CodeableConceptDt cpt = encounter == null ? null : FhirUtil.getFirst(encounter.getType());
        CodingDt coding = cpt == null ? null : FhirUtil.getFirst(cpt.getCoding());
        return coding == null ? null : coding.getCode();
    }

    public static boolean isLocked(Encounter encounter) {
        BoundCodeDt<EncounterStateEnum> status = encounter.getStatusElement();
        return status != null && status.getValueAsEnum() == EncounterStateEnum.FINISHED;
    }

    public static boolean isPrepared(Encounter encounter) {
        return encounter != null && !encounter.getLocation().isEmpty() && !encounter.getParticipant().isEmpty()
                && getServiceCategory(encounter) != null;
    }

    public static Encounter.Participant getParticipantByType(Encounter encounter, CodingDt participationType) {
        for (Encounter.Participant p : encounter.getParticipant()) {
            if (hasType(p, participationType)) {
                return p;
            }
        }

        return null;
    }

    public static boolean isPrimary(Encounter.Participant participant) {
        return hasType(participant, primaryType);
    }

    public static boolean removeType(Encounter.Participant participant, CodingDt participationType) {
        CodeableConceptDt cpt;
        boolean found = false;

        while ((cpt = findType(participant, participationType)) != null) {
            participant.getType().remove(cpt);
            found = true;
        }

        return found;
    }

    public static boolean addType(Encounter.Participant participant, CodingDt participationType) {
        if (!hasType(participant, participationType)) {
            CodeableConceptDt cpt = participant.addType();
            cpt.getCoding().add(participationType);
            return true;
        }

        return false;
    }

    public static boolean hasType(Encounter.Participant participant, CodingDt participationType) {
        return findType(participant, participationType) != null;
    }

    private static CodeableConceptDt findType(Encounter.Participant participant, CodingDt participationType) {
        if (participant != null) {
            for (CodeableConceptDt tp : participant.getType()) {
                for (CodingDt coding : tp.getCoding()) {
                    if (coding.getSystem().equals(participationType.getSystem())
                            && coding.getCode().equals(participationType.getCode())) {
                        return tp;
                    }
                }
            }
        }

        return null;
    }

    public static Encounter.Participant getPrimaryParticipant(Encounter encounter) {
        return getParticipantByType(encounter, primaryType);
    }

    public static HumanNameDt getName(Encounter.Participant participant) {
        IBaseResource resource = ClientUtil.getResource(participant.getIndividual());
        List<HumanNameDt> names = FhirUtil.getNames(resource);
        return names == null ? null : FhirUtil.getName(names);
    }

    public static Practitioner getPractitioner(Encounter.Participant participant) {
        if (participant == null) {
            return null;
        }

        ResourceReferenceDt resource = participant.getIndividual();
        IBaseResource ele = resource.getResource();
        return ele instanceof Practitioner ? (Practitioner) ele : null;
    }

    public static List<Practitioner> getPractitioners(List<Encounter.Participant> participants) {
        List<Practitioner> list = new ArrayList<>();

        for (Encounter.Participant participant : participants) {
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
     * @param encounter An encounter.
     * @param physicalType The physical location type sought.
     * @return The encounter location corresponding to the specified physical type, or null if none
     *         found.
     */
    public static Encounter.Location getLocationByPhysicalType(Encounter encounter, String physicalType) {
        for (Encounter.Location encounterLocation : encounter.getLocation()) {
            Location location = ClientUtil.getResource(encounterLocation.getLocation(), Location.class);

            if (physicalType.equals(FhirUtil.getFirst(location.getPhysicalType().getCoding()).getCode())) {
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
