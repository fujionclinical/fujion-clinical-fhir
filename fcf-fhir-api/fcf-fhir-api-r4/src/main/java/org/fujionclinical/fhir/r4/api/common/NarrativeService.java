/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2019 fujionclinical.org
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
package org.fujionclinical.fhir.r4.api.common;

import ca.uhn.fhir.context.FhirContext;
import org.fujionclinical.fhir.api.common.core.BaseNarrativeService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.INarrative;
import org.hl7.fhir.r4.model.DomainResource;

/**
 * Wraps hapi-fhir's narrative generator as a service.
 */
public class NarrativeService extends BaseNarrativeService {

    public NarrativeService(FhirContext fhirContext) {
        super(fhirContext);
    }

    /**
     * Returns a narrative from the resource, if one is available, or constructs one if not.
     *
     * @param resource Resource whose narrative is sought.
     * @return The narrative, or null if one is not available.
     */
    protected INarrative extractNarrative(IBaseResource resource) {
        return resource instanceof DomainResource ? ((DomainResource) resource).getText() : null;
    }

}
