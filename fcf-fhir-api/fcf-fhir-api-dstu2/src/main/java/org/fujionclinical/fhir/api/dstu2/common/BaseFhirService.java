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

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.fujionclinical.fhir.api.common.core.AbstractFhirService;
import org.fujionclinical.fhir.api.common.core.Constants;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

/**
 * Base service for accessing FHIR-based services.
 */
public class BaseFhirService extends AbstractFhirService<Patient, IdentifierDt, ResourceReferenceDt> {

    /**
     * Inject FHIR client.
     *
     * @param client The FHIR client.
     */
    public BaseFhirService(IGenericClient client) {
        super(client);
    }

    /**
     * FHIR request to create the given resource.
     *
     * @param <T>      Resource type.
     * @param resource Resource to create.
     * @return The created resource.
     */
    @Override
    public <T extends IBaseResource> T createResource(T resource) {
        MethodOutcome outcome = getClient().create().resource(resource).execute();
        return FhirUtilDstu2.processMethodOutcome(outcome, resource);
    }

    /**
     * Method creates a resource only if the resource with that identifier does not already exist.
     * At this time, the call appears to create the resource even when it already exists.
     *
     * @param resource   A FHIR resource.
     * @param identifier The resource identifier.
     * @return The outcome of the operation.
     */
    @Override
    public MethodOutcome createResourceIfNotExist(
            IBaseResource resource,
            IdentifierDt identifier) {
        return getClient().create().resource(resource).conditional()
                .where(Constants.PARAM_IDENTIFIER.exactly().systemAndIdentifier(identifier.getSystem(), identifier.getValue()))
                .execute();
    }

    /**
     * Deletes all resources of the given class that contain the identifier.
     *
     * @param <T>        Resource type.
     * @param identifier Resources with this identifier will be deleted.
     * @param clazz      Class of the resources to be searched.
     * @return Count of deleted resources.
     */
    public <T extends IBaseResource> int deleteResourcesByIdentifier(
            IdentifierDt identifier,
            Class<T> clazz) {
        List<T> resources = searchResourcesByIdentifier(identifier, clazz, MAX_COUNT);
        deleteResources(resources);
        return resources.size();
    }

    /**
     * Returns a list of all resources related to the specified resource (i.e., the $everything operation).
     *
     * @param resource The reference resource.
     * @return The resources related to the reference resource.
     */
    @Override
    public List<IBaseResource> everything(IBaseResource resource) {
        Parameters result = getClient()
                .operation()
                .onInstance(resource.getIdElement())
                .named("$everything")
                .withNoParameters(Parameters.class)
                .execute();

        Bundle bundle = (Bundle) result.getParameterFirstRep().getResource();
        return FhirUtilDstu2.getEntries(bundle);
    }

    /**
     * Returns a resource of the specified type given a resource reference. If the resource has not
     * been previously fetched, it will be fetched from the server. If the referenced resource is
     * not of the specified type, null is returned.
     *
     * @param <T>       Resource type.
     * @param reference A resource reference.
     * @param clazz     The desired resource class.
     * @return The corresponding resource.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IBaseResource> T getResource(
            ResourceReferenceDt reference,
            Class<T> clazz) {
        IBaseResource resource = getResource(reference);
        return clazz.isInstance(resource) ? (T) resource : null;
    }

    /**
     * Returns a resource given a resource reference. If the resource has not been previously
     * fetched, it will be fetched from the server.
     *
     * @param reference A resource reference.
     * @return The corresponding resource.
     */
    @Override
    public IBaseResource getResource(ResourceReferenceDt reference) {
        if (reference == null || reference.isEmpty()) {
            return null;
        }

        if (reference.getResource() != null) {
            return reference.getResource();
        }

        IBaseResource resource = getResource(reference.getReference());
        reference.setResource(resource);
        return resource;
    }

    /**
     * Returns all resources of the given class that contain the identifier.
     *
     * @param <T>      Resource type.
     * @param system   The identifier's system.
     * @param value    The identifier's value.
     * @param clazz    Class of the resources to be searched.
     * @param maxCount Maximum entries to return.
     * @return List of resources containing the identifier.
     */
    @Override
    public <T extends IBaseResource> List<T> searchResourcesByIdentifier(
            String system,
            String value,
            Class<T> clazz,
            int maxCount) {
        return searchResourcesByIdentifier(FhirUtilDstu2.createIdentifier(system, value), clazz, maxCount);
    }

    /**
     * Returns all resources of the given class that contain the identifier.
     *
     * @param <T>        Resource type.
     * @param identifier Resources with this identifier will be returned.
     * @param clazz      Class of the resources to be searched.
     * @param maxCount   Maximum entries to return.
     * @return List of resources containing the identifier.
     */
    @Override
    public <T extends IBaseResource> List<T> searchResourcesByIdentifier(
            IdentifierDt identifier,
            Class<T> clazz,
            int maxCount) {
        Bundle bundle = getClient().search().forResource(clazz).count(maxCount)
                .where(Constants.PARAM_IDENTIFIER.exactly().systemAndIdentifier(identifier.getSystem(), identifier.getValue()))
                .returnBundle(Bundle.class).execute();

        return FhirUtilDstu2.getEntries(bundle, clazz);
    }

    /**
     * Returns all resources that contain the tag.
     *
     * @param tag      Resources with this tag will be returned.
     * @param maxCount Maximum entries to return.
     * @return List of resources containing the tag.
     */
    @Override
    public List<IBaseResource> searchResourcesByTag(
            IBaseCoding tag,
            int maxCount) {
        Bundle bundle = getClient().search().forAllResources().count(maxCount).withTag(tag.getSystem(), tag.getCode())
                .returnBundle(Bundle.class).execute();

        return FhirUtilDstu2.getEntries(bundle, null, null);
    }

    /**
     * Returns all resources of the given class that contain the tag.
     *
     * @param <T>      Resource type.
     * @param tag      Resources with this tag will be returned.
     * @param clazz    Class of the resources to be searched.
     * @param maxCount Maximum entries to return.
     * @return List of resources containing the tag.
     */
    @Override
    public <T extends IBaseResource> List<T> searchResourcesByTag(
            IBaseCoding tag,
            Class<T> clazz,
            int maxCount) {
        Bundle bundle = getClient().search().forResource(clazz).count(maxCount).withTag(tag.getSystem(), tag.getCode())
                .returnBundle(Bundle.class).execute();

        return FhirUtilDstu2.getEntries(bundle, clazz);
    }

    /**
     * Returns all resources of the given class.
     *
     * @param <T>      Resource type.
     * @param clazz    Class of the resources to be searched.
     * @param maxCount Maximum entries to return.
     * @return List of resources containing the identifier.
     */
    @Override
    public <T extends IBaseResource> List<T> searchResourcesByType(
            Class<T> clazz,
            int maxCount) {
        Bundle bundle = getClient().search().forResource(clazz).count(maxCount).returnBundle(Bundle.class).execute();
        return FhirUtilDstu2.getEntries(bundle, clazz);
    }

    /**
     * Search for patient-based resources of the given class.
     *
     * @param <T>      Resource type.
     * @param patient  Patient to be searched.
     * @param clazz    Class of the resources to be returned.
     * @param maxCount Maximum entries to return.
     * @return List of matching resources.
     */
    @Override
    public <T extends IBaseResource> List<T> searchResourcesForPatient(
            Patient patient,
            Class<T> clazz,
            int maxCount) {
        Bundle bundle = getClient().search().forResource(clazz).count(maxCount)
                .where(Constants.PARAM_PATIENT.hasId(FhirUtilDstu2.getResourceIdPath(patient))).returnBundle(Bundle.class).execute();

        return FhirUtilDstu2.getEntries(bundle, clazz);
    }

    /**
     * FHIR request to update the given resource.
     *
     * @param <T>      Resource type.
     * @param resource Resource to update.
     * @return The updated resource.
     */
    @Override
    public <T extends IBaseResource> T updateResource(T resource) {
        MethodOutcome outcome = getClient().update().resource(FhirUtilDstu2.stripVersion(resource)).execute();
        return FhirUtilDstu2.processMethodOutcome(outcome, resource);
    }

    @Override
    protected void validateClient() {
        FhirUtilDstu2.assertFhirVersion(getClient());
    }

}
