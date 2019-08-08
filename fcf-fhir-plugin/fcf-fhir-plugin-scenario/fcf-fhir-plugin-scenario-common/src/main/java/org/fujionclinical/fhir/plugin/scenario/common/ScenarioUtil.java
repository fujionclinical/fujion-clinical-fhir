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
package org.fujionclinical.fhir.plugin.scenario.common;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.Tag;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class ScenarioUtil {
    
    public static final String DEMO_URN = "urn:fujion:hsp:model:demo";
    
    /**
     * Identifier used to locate demo resources for bulk deletes.
     */
    public static final Tag DEMO_GROUP_TAG = new Tag(DEMO_URN, "*", "Demo Data");
    
    /**
     * Convenience method to create a time offset.
     *
     * @param minuteOffset Offset in minutes.
     * @return A date minus the offset.
     */
    public static final Date createDateWithMinuteOffset(long minuteOffset) {
        return new Date(System.currentTimeMillis() - minuteOffset * 60 * 1000);
    }
    
    /**
     * Convenience method to create a time offset.
     *
     * @param dayOffset Offset in days.
     * @return A date minus the offset.
     */
    public static final Date createDateWithDayOffset(long dayOffset) {
        return createDateWithMinuteOffset(dayOffset * 24 * 60);
    }
    
    /**
     * Convenience method to create a time offset.
     *
     * @param yearOffset Offset in years.
     * @return A date minus the offset.
     */
    public static final Date createDateWithYearOffset(long yearOffset) {
        return createDateWithDayOffset(yearOffset * 365);
    }
    
    /**
     * Returns a random element from a string array.
     *
     * @param choices The array of possible choices.
     * @return A random element.
     */
    public static final String getRandom(String[] choices) {
        int index = (int) (Math.random() * choices.length);
        return choices[index];
    }

    /**
     * Adds a tag to a resource for bulk deletes of demo data.
     *
     * @param resource The resource.
     */
    public static final void addDemoTag(IBaseResource resource) {
        FhirUtil.addTag(DEMO_GROUP_TAG, resource);
    }

    /**
     * Adds a tag to a resource for scenario-based deletes of demo data.
     *
     * @param resource The resource.
     * @param scenario The scenario name.
     */
    public static final void addScenarioTag(IBaseResource resource, String scenario) {
        FhirUtil.addTag(createScenarioTag(scenario), resource);
    }

    /**
     * Copies any demo tags from source to destination.
     *
     * @param source The source resource.
     * @param destination The destination resource.
     */
    public static final void copyDemoTags(IBaseResource source, IBaseResource destination) {
        for (IBaseCoding tag : FhirUtil.getTagsBySystem(source, DEMO_URN)) {
            FhirUtil.addTag(tag, destination);
        }
    }

    /**
     * Creates a tag to be used for scenario-based deletes.
     *
     * @param scenario The scenario name.
     * @return The newly created tag.
     */
    public static final IBaseCoding createScenarioTag(String scenario) {
        return new Tag(DEMO_URN, scenario, "Scenario: " + scenario);
    }
    
    /**
     * This is a bit of a hack to enumerate all valid resource classes. It's used right now
     * because many FHIR servers don't implement cross-resource searches.
     *
     * @return Set of all valid resource classes.
     */
    protected static final Set<Class<? extends IBaseResource>> getResourceClasses(String packageName, Set<Class<? extends IBaseResource>> resourceClasses) {
        synchronized (resourceClasses) {
            if (resourceClasses.isEmpty()) {
                FastClasspathScanner fcs = new FastClasspathScanner(packageName);
                fcs.matchClassesImplementing(IResource.class, implementingClass -> {
                    if (!Modifier.isAbstract(implementingClass.getModifiers())) {
                        resourceClasses.add(implementingClass);
                    }
                });
                fcs.scan();
            }

            return Collections.unmodifiableSet(resourceClasses);
        }
    }
}
