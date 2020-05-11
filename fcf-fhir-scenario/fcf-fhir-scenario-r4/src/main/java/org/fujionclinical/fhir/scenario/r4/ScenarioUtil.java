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
package org.fujionclinical.fhir.scenario.r4;

import org.fujionclinical.fhir.api.r4.common.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

import java.util.List;

public class ScenarioUtil extends org.fujionclinical.fhir.scenario.common.ScenarioUtil {

    /**
     * Convenience method for creating identifiers in local system.
     *
     * @param system The identifier system.
     * @param value  The identifier value.
     * @return The newly created identifier.
     */
    public static Identifier createIdentifier(
            String system,
            Object value) {
        Identifier identifier = new Identifier();
        identifier.setSystem(DEMO_URN + ":" + system);
        identifier.setValue(value.toString());
        return identifier;
    }

    /**
     * Convenience method for creating identifiers for resources belonging to a patient. The
     * identifier generated will be unique across all resources.
     *
     * @param system  The identifier system.
     * @param idnum   The identifier value.
     * @param patient Owner of the resource to receive the identifier.
     * @return The newly created identifier.
     */
    public static Identifier createIdentifier(
            String system,
            int idnum,
            Patient patient) {
        String value = getMainIdentifier(patient).getValue() + "_" + idnum;
        return createIdentifier(system, value);
    }

    /**
     * Returns the principal identifier for the given resource.
     *
     * @param resource The resource whose main identifier is sought.
     * @return The main identifier, or null if not found.
     */
    public static Identifier getMainIdentifier(IBaseResource resource) {
        List<Identifier> identifiers = FhirUtil.getIdentifiers(resource);
        return FhirUtil.getFirst(identifiers);
    }

}
