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

import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.api.practitioner.IPractitioner;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class QueryTransforms {

    public interface PropertyPathTransform extends Function<String, String> {

    }

    public static abstract class MapTransform implements PropertyPathTransform {

        protected final Map<String, String> map = new HashMap<>();

        public MapTransform() {
            initMap();
        }

        protected abstract void initMap();

        @Override
        public String apply(String propertyPath) {
            return map.getOrDefault(propertyPath, propertyPath);
        }

    }

    public static final MapTransform DEFAULT_TRANSFORM = new MapTransform() {

        @Override
        protected void initMap() {
            map.put("id", "_id");
            map.put("identifiers", "identifier");
        }
    };

    public static final MapTransform ADDRESS_TRANSFORM = new MapTransform() {

        @Override
        protected void initMap() {
            map.put("addresses", "address");
        }
    };

    public static final MapTransform PERSON_TRANSFORM = new MapTransform() {

        @Override
        protected void initMap() {
            map.putAll(DEFAULT_TRANSFORM.map);
            map.putAll(ADDRESS_TRANSFORM.map);
            map.put("deceaseddate", "death-date");
            map.put("name", "name:");
            map.put("familyname", "family:");
            map.put("givennames", "given:");
        }
    };

    private static final Map<Class<?>, PropertyPathTransform> pathTransforms = new HashMap<>();

    static {
        pathTransforms.put(IPatient.class, PERSON_TRANSFORM);
        pathTransforms.put(IPractitioner.class, PERSON_TRANSFORM);
    }

    public static final PropertyPathTransform getTransform(Class<?> clazz) {
        PropertyPathTransform transform = pathTransforms.get(clazz);
        return transform == null ? DEFAULT_TRANSFORM : transform;
    }

    private QueryTransforms() {
    }

}
