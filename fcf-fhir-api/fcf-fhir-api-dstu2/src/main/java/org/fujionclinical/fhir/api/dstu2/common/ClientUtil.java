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
package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.api.spring.SpringUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * FHIR client utility methods.
 */
public class ClientUtil {

    private static volatile BaseFhirService fhirService;

    public static BaseFhirService getFhirService() {
        return SpringUtil.getBean("fhirService", BaseFhirService.class, () -> fhirService, value -> fhirService = value);
    }

    public static IGenericClient getFhirClient() {
        return getFhirService().getClient();
    }

    public static FhirContext getFhirContext() {
        return getFhirClient().getFhirContext();
    }

    /**
     * Returns a resource of the specified type given a resource reference. If the resource has not
     * been previously fetched, it will be fetched from the server. If the referenced resource is
     * not of the specified type, null is returned.
     *
     * @param <T>       The resource class.
     * @param reference A resource reference.
     * @param clazz     The desired resource class.
     * @return The corresponding resource.
     */
    public static <T extends IBaseResource> T getResource(
            ResourceReferenceDt reference,
            Class<T> clazz) {
        return getFhirService().getResource(reference, clazz);
    }

    /**
     * Returns a resource given a resource reference. If the resource has not been previously
     * fetched, it will be fetched from the server.
     *
     * @param reference A resource reference.
     * @return The corresponding resource.
     */
    public static IBaseResource getResource(ResourceReferenceDt reference) {
        return getFhirService().getResource(reference);
    }

    /**
     * Enforce static class.
     */
    private ClientUtil() {
    }

}
