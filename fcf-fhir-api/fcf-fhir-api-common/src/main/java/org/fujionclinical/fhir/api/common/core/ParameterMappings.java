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
import org.fujionclinical.api.model.core.IDomainType;
import org.fujionclinical.api.query.QueryOperator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ParameterMappings {

    private static ParameterMappings instance = new ParameterMappings();

    private final Map<String, String> map = new HashMap<>();

    private String fhirVersion;

    public static final ParameterMappings getInstance() {
        return instance;
    }

    public static String getParameterName(
            String propertyPath,
            QueryOperator operator,
            Class<? extends IDomainType> domainClass) {
        return instance.get(propertyPath, operator, domainClass);
    }

    /**
     * Map keys have one to three components of the form: [FHIR version]:[Class simple name]:[Property path].  Lookup
     * progressively removes each component from the beginning of the key until a match is found or the
     * last component has been processed.  The process is repeated for each superclass until a match is found
     * or the base superclass is encountered.
     * <p>
     * Map values ending in "~" use ":exact" modifier for the EQ operator.
     */
    private ParameterMappings() {
        map.put("id", "_id");
        map.put("identifiers", "identifier");
        map.put("addresses", "address");

        map.put("person:deceaseddate", "death-date");
        map.put("person:name", "name~");
        map.put("person:fullname", "name~");
        map.put("person:familyname", "family~");
        map.put("person:givennames", "given~");
    }

    private String get(
            String propertyPath,
            QueryOperator operator,
            Class<?> clazz) {
        propertyPath = propertyPath.toLowerCase();
        String paramName = null;

        while (paramName == null && clazz != null) {
            String key = fhirVersion + ":" + clazz.getSimpleName().toLowerCase().substring(1) + ":" + propertyPath;
            paramName = get(key);
            clazz = getSuperinterface(clazz);
        }

        if (paramName != null) {
            paramName = paramName.replace("~", operator == QueryOperator.EQ ? ":exact" : "");
        }

        return paramName == null ? propertyPath.replace(".", "-") : paramName;
    }

    private Class<?> getSuperinterface(Class<?> clazz) {
        return Arrays.stream(clazz.getInterfaces())
                .filter(intf -> intf != IDomainType.class && IDomainType.class.isAssignableFrom(intf))
                .findFirst()
                .orElse(null);
    }

    private String get(String key) {
        String value = null;

        do {
            value = map.get(key);

            if (value == null) {
                String[] pcs = key.split(":", 2);
                key = pcs.length == 1 ? null : pcs[1];
             }

        } while (value == null && key != null);

        return value;
    }

    public void setFhirContext(FhirContext fhirContext) {
        setFhirVersion(FhirUtil.getFhirVersion(fhirContext));
    }

    public void setFhirVersion(FhirVersionEnum fhirVersion) {
        this.fhirVersion = fhirVersion.name().toLowerCase();
    }

}
