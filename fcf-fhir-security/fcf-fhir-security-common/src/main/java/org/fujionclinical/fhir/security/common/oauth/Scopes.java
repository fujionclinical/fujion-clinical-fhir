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
package org.fujionclinical.fhir.security.common.oauth;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages collection of scopes.
 */
public class Scopes implements Serializable {

    private final List<String> scopes = new ArrayList<>();

    /**
     * Adds a scope to the collection.
     *
     * @param scope IScope to add.
     * @return Returns this (for chaining).
     */
    public Scopes add(IScope scope) {
        this.scopes.add(scope.asStringValue());
        return this;
    }

    /**
     * Returns the collection as a space-delimited list.
     *
     * @return Space-delimited list of scopes.
     */
    public String asParamValue() {
        return StringUtils.join(scopes, " ");
    }

}
