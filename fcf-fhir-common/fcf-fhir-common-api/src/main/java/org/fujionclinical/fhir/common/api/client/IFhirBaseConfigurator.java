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
package org.fujionclinical.fhir.common.api.client;

/**
 * The base interface for FHIR-based configurators.  Supports multiple configurators of the same
 * type, using an optional qualifier to segregate settings.  For example, one might configure a
 * FHIR client that accesses a FHIR-based terminology server and another one that accesses patient
 * data from a different server.
 */
public interface IFhirBaseConfigurator {

    /**
     * The qualifier for the configurator. This allows for multiple configurators, each with its
     * own segregated settings.
     *
     * @return The qualifier for the configurator. This must be a string that is unique within
     * configurators sharing the same property prefix and must contain only alphanumeric
     * characters or underscores.  The qualifier may also be null or empty.
     */
    String getQualifier();
    
    /**
     * Returns the prefix for property names.
     *
     * @return The prefix for property names.
     */
    String getPropertyPrefix();

}
