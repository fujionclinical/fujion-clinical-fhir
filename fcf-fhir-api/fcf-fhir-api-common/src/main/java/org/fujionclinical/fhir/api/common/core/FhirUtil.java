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

import org.apache.commons.io.IOUtils;
import org.coolmodel.mediator.fhir.common.FhirUtils;
import org.fujion.core.BeanUtil;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.coolmodel.mediator.fhir.common.FhirUtils.stripVersion;

public class FhirUtil {

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
     * Returns true if the resource has the specified tag.
     *
     * @param tag      Tag to find.
     * @param resource Resource to search.
     * @return True if the tag is present.
     **/
    public static boolean hasTag(
            IBaseCoding tag,
            IBaseResource resource) {
        return resource.getMeta().getTag(tag.getSystem(), tag.getCode()) != null;
    }

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
        if (!hasTag(tag, resource)) {
            IBaseCoding newTag = resource.getMeta().addTag();
            newTag.setCode(tag.getCode());
            newTag.setSystem(tag.getSystem());
            newTag.setDisplay(tag.getDisplay());
            return true;
        }

        return false;
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
     * Returns all resource tags belonging to the specified system.
     *
     * @param resource The resource.
     * @param system   The system.
     * @return A list of matching tags; never null;
     */
    public static List<IBaseCoding> getTagsBySystem(
            IBaseResource resource,
            String system) {
        return resource.getMeta().getTag().stream()
                .filter(tag -> system.equals(tag.getSystem()))
                .collect(Collectors.toList());
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
        return resource.getMeta().getTag().stream()
                .filter(tag -> system.equals(tag.getSystem()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the base 64-encoded equivalent of a resource.
     *
     * @param resourceName The resource name.
     * @return The base 64-encoded resource.
     */
    public static byte[] getResourceAsBase64(String resourceName) {
        return Base64.getEncoder().encode(getResourceAsByteArray(resourceName));
    }

    /**
     * Returns the file resource as a byte array.
     *
     * @param resourceName The path of the file resource.
     * @return The resource as a byte array.
     */
    @SuppressWarnings("all")
    public static byte[] getResourceAsByteArray(String resourceName) {
        try (InputStream is = FhirUtils.class.getClassLoader().getResource(resourceName).openStream()) {
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing file " + resourceName, e);
        }
    }

    /**
     * Returns the value of a property that returns a list from an object.
     *
     * @param <T>          The property value type.
     * @param object       The object containing the property.
     * @param propertyName The name of the property.
     * @return The value of the property. A null return value may mean the property does not exist
     * or the property getter returned null. Will never throw an exception.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getListProperty(
            Object object,
            String propertyName) {
        try {
            Object value = BeanUtil.getPropertyValue(object, propertyName);
            return value == null ? null : value instanceof List ? (List<T>) value : Collections.singletonList((T) value);
        } catch (Exception e) {
            return null;
        }
    }

    private FhirUtil() {
    }

}
