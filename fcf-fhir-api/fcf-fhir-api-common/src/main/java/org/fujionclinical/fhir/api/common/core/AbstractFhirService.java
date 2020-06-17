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
package org.fujionclinical.fhir.api.common.core;

import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.fujionclinical.api.messaging.Message;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Version-agnostic base service for FHIR services.
 */
public abstract class AbstractFhirService<PATIENT, IDENTIFIER, REFERENCE> {

    protected static final int DEFAULT_COUNT = 100;

    protected static final int MAX_COUNT = Integer.MAX_VALUE;

    private final IGenericClient client;

    /**
     * Inject FHIR client.
     *
     * @param client The FHIR client.
     */
    public AbstractFhirService(IGenericClient client) {
        this.client = client;
        validateClient();
    }

    /**
     * FHIR request to create or update the given resource. If the resource has no logical
     * identifier, a create operation is requested. Otherwise, an update operation is requested.
     *
     * @param <T>      Resource type.
     * @param resource Resource to create or update.
     * @return The resource resulting from the operation.
     */
    public <T extends IBaseResource> T createOrUpdateResource(T resource) {
        return resource.getIdElement().isEmpty() ? createResource(resource) : updateResource(resource);
    }

    /**
     * FHIR request to create the given resource.
     *
     * @param <T>      Resource type.
     * @param resource Resource to create.
     * @return The created resource.
     */
    protected abstract <T extends IBaseResource> T createResource(T resource);

    /**
     * Method creates a resource only if the resource with that identifier does not already exist.
     * At this time, the call appears to create the resource even when it already exists.
     *
     * @param resource   A FHIR resource.
     * @param identifier The resource identifier.
     * @return The outcome of the operation.
     */
    protected abstract MethodOutcome createResourceIfNotExist(
            IBaseResource resource,
            IDENTIFIER identifier);

    /**
     * Deletes a resource.
     *
     * @param resource Resource to delete.
     * @return The outcome of the operation.
     */
    public MethodOutcome deleteResource(IBaseResource resource) {
        return getClient().delete().resource(resource).execute();
    }

    /**
     * Deletes all resources in the provided list.
     *
     * @param resources Resources to delete.
     */
    public void deleteResources(List<? extends IBaseResource> resources) {
        for (IBaseResource resource : resources) {
            deleteResource(resource);
        }
    }

    /**
     * Deletes all resources of the given class that contain the identifier.
     *
     * @param <T>        Resource type.
     * @param identifier Resources with this identifier will be deleted.
     * @param clazz      Class of the resources to be searched.
     * @return Count of deleted resources.
     */
    protected abstract <T extends IBaseResource> int deleteResourcesByIdentifier(
            IDENTIFIER identifier,
            Class<T> clazz);

    /**
     * Deletes all resources contain the tag.
     *
     * @param tag Resources with this tag will be deleted.
     * @return Count of deleted resources.
     */
    public int deleteResourcesByTag(IBaseCoding tag) {
        return deleteResourcesByTag(tag, null);
    }

    /**
     * Deletes all resources of the given class that contain the tag.
     *
     * @param <T>   Resource type.
     * @param tag   Resources with this tag will be deleted.
     * @param clazz Class of the resources to be searched (null for all).
     * @return Count of deleted resources.
     */
    public <T extends IBaseResource> int deleteResourcesByTag(
            IBaseCoding tag,
            Class<T> clazz) {
        List<? extends IBaseResource> resources = clazz == null ? searchResourcesByTag(tag, MAX_COUNT)
                : searchResourcesByTag(tag, clazz, MAX_COUNT);
        deleteResources(resources);
        return resources.size();
    }

    /**
     * Returns a list of all resources related to the specified resource (i.e., the $everything operation).
     *
     * @param resource The reference resource.
     * @return The resources related to the reference resource.
     */
    protected abstract List<IBaseResource> everything(IBaseResource resource);

    /**
     * For urls without a service root, prepends the default service root.
     *
     * @param url URL to expand.
     * @return URL with a service root prepended.
     */
    public String expandURL(String url) {
        return url.contains(":/") ? url : FhirUtil.concatPath(getServiceRoot(), url);
    }

    /**
     * Returns the FHIR client.
     *
     * @return The FHIR client.
     */
    public IGenericClient getClient() {
        return client;
    }

    /**
     * Returns the resource corresponding to the given id.
     *
     * @param id The resource id.
     * @return The corresponding resource, if found.
     */
    public IBaseResource getResource(IIdType id) {
        Assert.state(id != null && !id.isEmpty(), "No resource id");
        return getClient().read().resource(id.getResourceType()).withId(id).execute();
    }

    /**
     * Returns the resource corresponding to the given id.
     *
     * @param <T>   The resource type.
     * @param clazz The resource type.
     * @param id    The resource id.
     * @return The corresponding resource, if found.
     */
    public <T extends IBaseResource> T getResource(
            Class<T> clazz,
            String id) {
        return getClient().read().resource(clazz).withId(id).execute();
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
    public abstract <T extends IBaseResource> T getResource(
            REFERENCE reference,
            Class<T> clazz);

    /**
     * Returns a resource given a resource reference. If the resource has not been previously
     * fetched, it will be fetched from the server.
     *
     * @param reference A resource reference.
     * @return The corresponding resource.
     */
    protected abstract IBaseResource getResource(REFERENCE reference);

    /**
     * Returns the default FHIR service root url.
     *
     * @return Default FHIR service root url.
     */
    public String getServiceRoot() {
        return ((GenericClient) getClient()).getUrlBase();
    }

    /**
     * Extracts a FHIR resource from a message.
     *
     * @param message Message containing a FHIR resource.
     * @return The extracted resource.
     */
    public IBaseResource messageToResource(Message message) {
        if ("application/json+fhir".equals(message.getType())) {
            return client.getFhirContext().newJsonParser().parseResource(message.getPayload().toString());
        } else if ("application/xml+fhir".equals(message.getType())) {
            return client.getFhirContext().newXmlParser().parseResource(message.getPayload().toString());
        } else {
            throw new DataFormatException(message.getType());
        }
    }

    /**
     * Extracts a FHIR resource from a message.
     *
     * @param <T>          Resource type.
     * @param message      Message containing a FHIR resource.
     * @param resourceType The expected resource type.
     * @return The extracted resource.
     */
    public <T extends IBaseResource> T messageToResource(
            Message message,
            Class<T> resourceType) {
        if ("application/json+fhir".equals(message.getType())) {
            return client.getFhirContext().newJsonParser().parseResource(resourceType, message.getPayload().toString());
        } else if ("application/xml+fhir".equals(message.getType())) {
            return client.getFhirContext().newXmlParser().parseResource(resourceType, message.getPayload().toString());
        } else {
            throw new DataFormatException(message.getType());
        }
    }

    /**
     * Package a resource into a message for transport via the messaging subsystem.
     *
     * @param resource The resource to package.
     * @param asJSON   If true, serialize to JSON format; otherwise, to XML format;
     * @return The newly created message.
     */
    public Message resourceToMessage(
            IBaseResource resource,
            boolean asJSON) {
        if (asJSON) {
            String data = client.getFhirContext().newJsonParser().encodeResourceToString(resource);
            return new Message("application/json+fhir", data);
        } else {
            String data = client.getFhirContext().newXmlParser().encodeResourceToString(resource);
            return new Message("application/xml+fhir", data);
        }
    }

    /**
     * Query for resources.
     *
     * @param <T>           The resource type.
     * @param resourceClass The type of resource.
     * @param queryString   The FHIR query string.
     * @return The result of the query.
     */
    protected <T extends IBaseResource> IQuery<IBaseBundle> searchResources(
            Class<T> resourceClass,
            String queryString) {
        String resource = getClient().getFhirContext().getResourceDefinition(resourceClass).getName();
        return getClient().search().byUrl(resource + "?" + queryString);
    }

    /**
     * Fetch multiple resources of the same type from the data store.
     *
     * @param <T>           The resource type.
     * @param resourceClass The type of resource.
     * @param ids           A list of logical ids.
     * @return The result of the query.
     */
    protected <T extends IBaseResource> IQuery<IBaseBundle> searchResourcesById(
            Class<T> resourceClass,
            String... ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }

        StringClientParam param = new StringClientParam("_id");
        return getClient().search().forResource(resourceClass).where(param.matches().values(ids));
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
    protected abstract <T extends IBaseResource> List<T> searchResourcesByIdentifier(
            String system,
            String value,
            Class<T> clazz,
            int maxCount);

    /**
     * Returns all resources of the given class that contain the identifier.
     *
     * @param <T>        Resource type.
     * @param identifier Resources with this identifier will be returned.
     * @param clazz      Class of the resources to be searched.
     * @return List of resources containing the identifier.
     */
    public <T extends IBaseResource> List<T> searchResourcesByIdentifier(
            IDENTIFIER identifier,
            Class<T> clazz) {
        return searchResourcesByIdentifier(identifier, clazz, DEFAULT_COUNT);
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
    protected abstract <T extends IBaseResource> List<T> searchResourcesByIdentifier(
            IDENTIFIER identifier,
            Class<T> clazz,
            int maxCount);

    /**
     * Returns all resources of the given class that contain the identifier.
     *
     * @param <T>    Resource type.
     * @param system The identifier's system.
     * @param value  The identifier's value.
     * @param clazz  Class of the resources to be searched.
     * @return List of resources containing the identifier.
     */
    public <T extends IBaseResource> List<T> searchResourcesByIdentifier(
            String system,
            String value,
            Class<T> clazz) {
        return searchResourcesByIdentifier(system, value, clazz, DEFAULT_COUNT);
    }

    /**
     * Returns all resources that contain the tag.
     *
     * @param tag      Resources with this tag will be returned.
     * @param maxCount Maximum entries to return.
     * @return List of resources containing the tag.
     */
    protected abstract List<IBaseResource> searchResourcesByTag(
            IBaseCoding tag,
            int maxCount);

    /**
     * Returns all resources of the given class that contain the tag.
     *
     * @param <T>      Resource type.
     * @param tag      Resources with this tag will be returned.
     * @param clazz    Class of the resources to be searched.
     * @param maxCount Maximum entries to return.
     * @return List of resources containing the tag.
     */
    protected abstract <T extends IBaseResource> List<T> searchResourcesByTag(
            IBaseCoding tag,
            Class<T> clazz,
            int maxCount);

    /**
     * Returns all resources that contain the tag.
     *
     * @param tag Resources with this tag will be returned.
     * @return List of resources containing the tag.
     */
    public List<IBaseResource> searchResourcesByTag(IBaseCoding tag) {
        return searchResourcesByTag(tag, DEFAULT_COUNT);
    }

    /**
     * Returns all resources of the given class that contain the tag.
     *
     * @param <T>   Resource type.
     * @param tag   Resources with this tag will be returned.
     * @param clazz Class of the resources to be searched.
     * @return List of resources containing the tag.
     */
    public <T extends IBaseResource> List<T> searchResourcesByTag(
            IBaseCoding tag,
            Class<T> clazz) {
        return searchResourcesByTag(tag, clazz, DEFAULT_COUNT);
    }

    /**
     * Returns all resources of the given class.
     *
     * @param <T>      Resource type.
     * @param clazz    Class of the resources to be searched.
     * @param maxCount Maximum entries to return.
     * @return List of resources containing the identifier.
     */
    protected abstract <T extends IBaseResource> List<T> searchResourcesByType(
            Class<T> clazz,
            int maxCount);

    /**
     * Returns all resources of the given class.
     *
     * @param <T>   Resource type.
     * @param clazz Class of the resources to be searched.
     * @return List of resources containing the identifier.
     */
    public <T extends IBaseResource> List<T> searchResourcesByType(Class<T> clazz) {
        return searchResourcesByType(clazz, DEFAULT_COUNT);
    }

    /**
     * Search for patient-based resources of the given class.
     *
     * @param <T>     Resource type.
     * @param patient Patient to be searched.
     * @param clazz   Class of the resources to be returned.
     * @return List of matching resources.
     */
    public <T extends IBaseResource> List<T> searchResourcesForPatient(
            PATIENT patient,
            Class<T> clazz) {
        return searchResourcesForPatient(patient, clazz, DEFAULT_COUNT);
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
    protected abstract <T extends IBaseResource> List<T> searchResourcesForPatient(
            PATIENT patient,
            Class<T> clazz,
            int maxCount);

    /**
     * FHIR request to update the given resource.
     *
     * @param <T>      Resource type.
     * @param resource Resource to update.
     * @return The updated resource.
     */
    protected abstract <T extends IBaseResource> T updateResource(T resource);

    /**
     * Validates that the client is of the expected FHIR version.
     */
    protected abstract void validateClient();

}
