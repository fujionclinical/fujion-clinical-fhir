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
package org.fujionclinical.fhir.api.common.query;

import ca.uhn.fhir.rest.gclient.IQuery;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

/**
 * Extended resource query interface.
 *
 * @param <R> The resource class
 * @param <C> The criteria class.
 */
public interface IResourceQueryEx<R extends IBaseResource, C> extends IResourceQuery<R, C> {


    /**
     * Alternative method for performing a search that allows for external configuration of the
     * query object.
     *
     * @param query The query object.
     * @return List of matching resources. May return null to indicate no matches.
     */
    List<R> search(IQuery<?> query);

    /**
     * Creates an empty query object for this resource class.
     *
     * @return The newly created query object.
     */
    IQuery<?> createQuery();
}
