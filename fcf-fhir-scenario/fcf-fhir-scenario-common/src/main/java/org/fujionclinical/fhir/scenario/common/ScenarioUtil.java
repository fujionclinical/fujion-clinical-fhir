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
package org.fujionclinical.fhir.scenario.common;

import ca.uhn.fhir.model.api.Tag;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;

public class ScenarioUtil {

    protected static final String SCENARIO_URN = "urn:fujionclinical:scenario";

    private static final String RESOURCE_URN = SCENARIO_URN + ":resource";

    /**
     * Identifier used to locate scenario resources for bulk deletes.
     */
    private static final Tag SCENARIO_GROUP_TAG = new Tag(SCENARIO_URN, "*", "Scenario Data");

    /**
     * Convenience method to create a time offset.
     *
     * @param minuteOffset Offset in minutes.
     * @return A date minus the offset.
     */
    public static Date createDateWithMinuteOffset(long minuteOffset) {
        return new Date(System.currentTimeMillis() - minuteOffset * 60 * 1000);
    }

    /**
     * Convenience method to create a time offset.
     *
     * @param dayOffset Offset in days.
     * @return A date minus the offset.
     */
    public static Date createDateWithDayOffset(long dayOffset) {
        return createDateWithMinuteOffset(dayOffset * 24 * 60);
    }

    /**
     * Convenience method to create a time offset.
     *
     * @param yearOffset Offset in years.
     * @return A date minus the offset.
     */
    public static Date createDateWithYearOffset(long yearOffset) {
        return createDateWithDayOffset(yearOffset * 365);
    }

    /**
     * Returns a random element from a string array.
     *
     * @param choices The array of possible choices.
     * @return A random element.
     */
    public static String getRandom(String[] choices) {
        int index = (int) (Math.random() * choices.length);
        return choices[index];
    }

    /**
     * Adds a tag to a resource for bulk deletes of scenario data.
     *
     * @param resource The resource.
     */
    public static void addTag(IBaseResource resource) {
        FhirUtil.addTag(SCENARIO_GROUP_TAG, resource);
    }

    /**
     * Copies any scenario tags from source to destination.
     *
     * @param source      The source resource.
     * @param destination The destination resource.
     */
    public static void copyScenarioTags(
            IBaseResource source,
            IBaseResource destination) {
        for (IBaseCoding tag : source.getMeta().getTag()) {
            if (tag.getSystem().startsWith(SCENARIO_URN)) {
                FhirUtil.addTag(tag, destination);
            }
        }
    }

    /**
     * Creates a tag to be used for scenario-based deletes.
     *
     * @param scenarioId   The scenario unique id.
     * @param scenarioName The scenario name.
     * @return The newly created tag.
     */
    public static IBaseCoding createScenarioTag(
            String scenarioId,
            String scenarioName) {
        return new Tag(SCENARIO_URN, scenarioId, "Scenario: " + scenarioName);
    }

    /**
     * Creates a tag to be used to identify named resources.
     *
     * @param resourceName The resource name.
     * @return The newly created tag.
     */
    public static IBaseCoding createNamedResourceTag(String resourceName) {
        return resourceName == null ? null : new Tag(RESOURCE_URN, resourceName, "Resource: " + resourceName);
    }

    /**
     * Returns the scenario associated with the resource.
     *
     * @param resource  The resource to examine.
     * @param scenarios A map of registered scenarios.
     * @return The associated scenario, or null if none.
     */
    public static <SCENARIO extends ScenarioBase<?>> SCENARIO getScenario(
            IBaseResource resource,
            Map<String, SCENARIO> scenarios) {
        SCENARIO scenario = null;

        for (IBaseCoding tag : FhirUtil.getTagsBySystem(resource, SCENARIO_URN)) {
            if ((scenario = scenarios.get(tag.getCode())) != null) {
                break;
            }
        }

        return scenario;
    }

    public static <T> T getParam(
            Map<String, T> map,
            String param) {
        return getParam(map, param, true);
    }

    public static <T> T getParam(
            Map<String, T> map,
            String param,
            boolean required) {
        T value = map.get(param);
        Assert.isTrue(!required || value != null, () -> "Missing configuration parameter: " + param);
        return value;
    }

}
