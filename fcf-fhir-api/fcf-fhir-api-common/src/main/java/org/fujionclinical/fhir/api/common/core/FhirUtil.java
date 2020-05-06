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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.UrlUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.fujion.common.Logger;
import org.fujion.component.Image;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FHIR utility methods.
 */
public class FhirUtil {

    protected static final Logger log = Logger.create(FhirUtil.class);

    /**
     * Adds a tag to a resource if not already present.
     *
     * @param tag      Tag to add.
     * @param resource Resource to receive tag.
     * @return True if the tag was added.
     */
    public static boolean addTag(
            IBaseCoding tag,
            IBaseResource resource) {
        boolean exists = resource.getMeta().getTag(tag.getSystem(), tag.getCode()) != null;

        if (!exists) {
            IBaseCoding newTag = resource.getMeta().addTag();
            newTag.setCode(tag.getCode());
            newTag.setSystem(tag.getSystem());
            newTag.setDisplay(tag.getDisplay());
        }

        return !exists;
    }

    /**
     * Returns the first resource tag matching the specified system.
     *
     * @param resource The resource.
     * @param system   The system.
     * @return The first matching tag or null if none found.
     */
    public static IBaseCoding getTagBySystem(
            IBaseResource resource,
            String system) {
        for (IBaseCoding coding : resource.getMeta().getTag()) {
            if (system.equals(coding.getSystem())) {
                return coding;
            }
        }

        return null;
    }

    /**
     * Returns all resource tags belonging to the specified system.
     *
     * @param resource The resource.
     * @param system   The system.
     * @return A list of matching tags; never null;
     */
    public static List<IBaseCoding> getTagsBySystem(
            IBaseResource resource,
            String system) {
        List<IBaseCoding> result = new ArrayList<>();

        for (IBaseCoding coding : resource.getMeta().getTag()) {
            if (system.equals(coding.getSystem())) {
                result.add(coding);
            }
        }

        return result;
    }

    /**
     * Performs an equality check on two resources using their id's.
     *
     * @param <T>  Resource type.
     * @param res1 The first resource.
     * @param res2 The second resource.
     * @return True if the two resources have equal id's.
     */
    public static <T extends IBaseResource> boolean areEqual(
            T res1,
            T res2) {
        return areEqual(res1, res2, false);
    }

    /**
     * Performs an equality check on two resources using their id's.
     *
     * @param <T>           Resource type.
     * @param res1          The first resource.
     * @param res2          The second resource.
     * @param ignoreVersion If true, ignore any version qualifiers in the comparison.
     * @return True if the two resources have equal id's.
     */
    public static <T extends IBaseResource> boolean areEqual(
            T res1,
            T res2,
            boolean ignoreVersion) {
        if (res1 == null || res2 == null) {
            return false;
        }

        return res1 == res2 || getIdAsString(res1, ignoreVersion).equals(getIdAsString(res2, ignoreVersion));
    }

    /**
     * Returns true if the resource is assignment-compatible with one of the classes list.
     *
     * @param <T>      Resource type.
     * @param classes  List of classes to check.
     * @param resource The resource to test.
     * @return True if the resource is assignment-compatible with one of the classes in the list.
     */
    public static <T extends IBaseResource> boolean classMatches(
            List<Class<T>> classes,
            IBaseResource resource) {
        for (Class<T> clazz : classes) {
            if (clazz.isInstance(resource)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Concatenates a path fragment to a root path. Ensures that a single "/" character separates
     * the two parts.
     *
     * @param root     The root path.
     * @param fragment The path fragment.
     * @return The concatenated result.
     */
    public static String concatPath(
            String root,
            String fragment) {
        while (root.endsWith("/")) {
            root = root.substring(0, root.length() - 1);
        }

        while (fragment.startsWith("/")) {
            fragment = fragment.substring(1);
        }

        return root + "/" + fragment;
    }

    /**
     * Returns the first element in a list, or null if there is none.
     *
     * @param <T>  List element type.
     * @param list A list.
     * @return The first list element, or null if none.
     */
    public static <T> T getFirst(List<T> list) {
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    /**
     * Returns the string representation of the id.
     *
     * @param id           The id.
     * @param stripVersion If true and the id has a version qualifier, remove it.
     * @return The string representation of the id.
     */
    public static String getIdAsString(
            IIdType id,
            boolean stripVersion) {
        String result = id == null ? null : id.getValueAsString();
        return result == null ? "" : stripVersion && id.hasVersionIdPart() ? stripVersion(result) : result;
    }

    /**
     * Returns the string representation of the resource's id.
     *
     * @param <T>          Resource type.
     * @param resource     The resource.
     * @param stripVersion If true and the id has a version qualifier, remove it.
     * @return The string representation of the resource's id.
     */
    public static <T extends IBaseResource> String getIdAsString(
            T resource,
            boolean stripVersion) {
        return getIdAsString(resource.getIdElement(), stripVersion);
    }

    /**
     * Returns the last element in a list, or null if there is none.
     *
     * @param <T>  List element type.
     * @param list A list.
     * @return The last list element, or null if none.
     */
    public static <T> T getLast(List<T> list) {
        return list == null || list.isEmpty() ? null : list.get(list.size() - 1);
    }

    /**
     * Returns the value of a property from a resource base.
     *
     * @param <T>           The property value type.
     * @param resource      The resource containing the property.
     * @param getter        The name of the getter method for the property.
     * @param expectedClass The expected class of the property value (null for any).
     * @return The value of the property. A null return value may mean the property does not exist
     *         or the property getter returned null. Will never throw an exception.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(
            IBaseResource resource,
            String getter,
            Class<T> expectedClass) {
        Object result = null;

        try {
            result = MethodUtils.invokeMethod(resource, getter, (Object[]) null);
            result = result == null || expectedClass == null ? result : expectedClass.isInstance(result) ? result : null;
        } catch (Exception e) {
        }

        return (T) result;
    }

    /**
     * Returns the value of a property that returns a list from a resource base.
     *
     * @param <T>          The property value type.
     * @param resource     The resource containing the property.
     * @param propertyName The name of the property.
     * @param itemClass    The expected class of the list elements.
     * @return The value of the property. A null return value may mean the property does not exist
     *         or the property getter returned null. Will never throw an exception.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getListProperty(
            IBaseResource resource,
            String propertyName,
            Class<T> itemClass) {
        try {
            Object value = PropertyUtils.getSimpleProperty(resource, propertyName);
            return value == null ? null : value instanceof List ? (List<T>) value : Collections.singletonList((T) value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the base 64-encoded equivalent of a resource.
     *
     * @param resourceName The resource name.
     * @return The base 64-encoded resource.
     */
    public static byte[] getResourceAndConvertToBase64(String resourceName) {
        return Base64.encodeBase64(getResourceAsByteArray(resourceName));
    }

    /**
     * Returns the file resource as a byte array.
     *
     * @param resourceName The path of the file resource.
     * @return The resource as a byte array.
     */
    public static byte[] getResourceAsByteArray(String resourceName) {
        try (InputStream is = FhirUtil.class.getClassLoader().getResource(resourceName).openStream()) {
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing file " + resourceName, e);
        }
    }

    /**
     * Returns the resource ID relative path.
     *
     * @param resource The resource.
     * @return The resource's relative path.
     */
    public static String getResourceIdPath(IBaseResource resource) {
        return getResourceIdPath(resource, true);
    }

    /**
     * Returns the resource ID relative path.
     *
     * @param resource     The resource.
     * @param stripVersion If true and the id has a version qualifier, remove it.
     * @return The resource's relative path.
     */
    public static String getResourceIdPath(
            IBaseResource resource,
            boolean stripVersion) {
        String id = resource.getIdElement().getResourceType() + "/" + resource.getIdElement().getIdPart();
        return stripVersion ? stripVersion(id) : id;
    }

    /**
     * Returns the resource type from a resource.
     *
     * @param resource The resource.
     * @return The type of resource.
     */
    public static String getResourceType(IBaseResource resource) {
        return resource == null ? null : getResourceType(resource.getIdElement());
    }

    /**
     * Extracts a resource type from an id.
     *
     * @param id The identifier
     * @return The resource type.
     */
    public static String getResourceType(IIdType id) {
        return id == null || id.isEmpty() ? null : id.getResourceType();
    }

    /**
     * Returns the expected resource type to be returned by the specified URL.
     *
     * @param url The URL.
     * @return The expected resource type.
     */
    public static String getResourceType(String url) {
        UrlUtil.UrlParts parts = UrlUtil.parseUrl(url);
        String resourceId = parts.getResourceId();
        return resourceId == null || resourceId.isEmpty() ? "Bundle" : parts.getResourceType();
    }

    /**
     * Casts an unspecified data type to a specific data type if possible.
     *
     * @param <V>   The original value type.
     * @param <T>   The target value type.
     * @param value The value to cast.
     * @param clazz The type to cast to.
     * @return The value cast to the specified type, or null if not possible.
     */
    @SuppressWarnings("unchecked")
    public static <V, T extends V> T getTyped(
            V value,
            Class<T> clazz) {
        return clazz.isInstance(value) ? (T) value : null;
    }

    /**
     * Removes a tag from a resource if present.
     *
     * @param tag      Tag to remove.
     * @param resource Resource to containing tag.
     * @return True if the tag was removed.
     */
    public static boolean removeTag(
            IBaseCoding tag,
            IBaseResource resource) {
        IBaseCoding theTag = resource.getMeta().getTag(tag.getSystem(), tag.getCode());

        if (theTag != null) {
            resource.getMeta().getTag().remove(theTag);
            return true;
        }

        return false;
    }

    /**
     * Strips the version qualifier from an id, if present.
     *
     * @param id The id.
     * @return The id without a version qualifier.
     */
    public static String stripVersion(String id) {
        int i = id.lastIndexOf("/_history");
        return i == -1 ? id : id.substring(0, i);
    }

    /**
     * Strips the version qualifier from a resource, if present.
     *
     * @param <T>      Resource type.
     * @param resource The resource.
     * @return The input resource, possibly modified.
     */
    public static <T extends IBaseResource> T stripVersion(T resource) {
        IIdType id = resource.getIdElement();

        if (id.hasVersionIdPart()) {
            id.setValue(stripVersion(id.getValue()));
            resource.setId(id);
        }

        return resource;
    }

    /**
     * Converts a list of objects to a list of their string equivalents.
     *
     * @param source The source list.
     * @return A list of string equivalents.
     */
    public static List<String> toStringList(List<?> source) {
        List<String> dest = new ArrayList<>(source.size());

        for (Object value : source) {
            dest.add(value.toString());
        }

        return dest;
    }

    /**
     * Returns the FHIR version of the client.
     *
     * @param fhirClient The FHIR client.
     * @return The FHIR version.
     */
    public static FhirVersionEnum getFhirVersion(IGenericClient fhirClient) {
        return getFhirVersion(fhirClient.getFhirContext());
    }

    /**
     * Returns the FHIR version of the context.
     *
     * @param fhirContext The FHIR context.
     * @return The FHIR version.
     */
    public static FhirVersionEnum getFhirVersion(FhirContext fhirContext) {
        return fhirContext.getVersion().getVersion();
    }

    /**
     * Asserts that the actual and the expected FHIR versions are the same.  Throws
     * an exception if not.
     *
     * @param fhirClient The FHIR client.
     * @param expected   The expected version.
     * @throws IllegalStateException If the versions do not match.
     */
    public static void assertFhirVersion(
            IGenericClient fhirClient,
            FhirVersionEnum expected) {
        assertFhirVersion(fhirClient.getFhirContext(), expected);
    }

    /**
     * Asserts that the actual and the expected FHIR versions are the same.  Throws
     * an exception if not.
     *
     * @param fhirContext The FHIR context.
     * @param expected    The expected version.
     * @throws IllegalStateException If the versions do not match.
     */
    public static void assertFhirVersion(
            FhirContext fhirContext,
            FhirVersionEnum expected) {
        FhirVersionEnum found = getFhirVersion(fhirContext);

        Assert.state(getFhirVersion(fhirContext) == expected, () ->
                "FHIR version mismatch.  Expected " + expected + " but found " + found);
    }

    public static Image getImage(String url) {
        return new Image(url);
    }

    /**
     * Enforce static class.
     */
    protected FhirUtil() {
    }
}
