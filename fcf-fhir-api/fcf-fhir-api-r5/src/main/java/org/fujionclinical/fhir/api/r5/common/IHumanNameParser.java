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
package org.fujionclinical.fhir.api.r5.common;

import org.hl7.fhir.r5.model.HumanName;

public interface IHumanNameParser {

    /**
     * Converts human name to displayable text.
     *
     * @param name Human name.
     * @return Displayable text.
     */
    String toString(HumanName name);

    /**
     * Converts text to a human name equivalent.
     *
     * @param value Value to convert.
     * @param name  Human name instance to receive parsed result (if null, one will be created).
     * @return The human name parsed from the input value.
     */
    HumanName fromString(
            String value,
            HumanName name);

    /**
     * Converts text to a human name equivalent.
     *
     * @param value Value to convert.
     * @return The human name parsed from the input value.
     */
    default HumanName fromString(String value) {
        return fromString(value, null);
    }

}
