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

import ca.uhn.fhir.rest.gclient.IQuery;
import org.fujionclinical.api.model.IDomainDAO;
import org.fujionclinical.api.model.IDomainObject;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * DAO for FHIR resources.
 */
public abstract class AbstractResourceDAO<T extends IDomainObject, R extends IBaseResource> implements IDomainDAO<T> {

    protected final Class<R> resourceClass;

    protected final Class<T> domainClass;

    private final AbstractFhirService fhirService;

    protected AbstractResourceDAO(
            AbstractFhirService fhirService,
            Class<T> domainClass,
            Class<R> resourceClass) {
        this.fhirService = fhirService;
        this.domainClass = domainClass;
        this.resourceClass = resourceClass;
    }

    /**
     * Fetch an instance of the domain class from the data store.
     */
    @Override
    public T read(String id) {
        return convert((R) fhirService.getResource(resourceClass, id));
    }

    protected abstract T convert(R resource);

    protected abstract R convert(T domainResource);

    @Override
    public T create(T template) {
        return convert((R) fhirService.createResource(convert(template)));
    }

    /**
     * Fetch multiple instances of the domain class from the data store.
     *
     * @param ids A list of ids to fetch.
     * @return The result of the query.
     */
    protected IQuery<IBaseBundle> query(String... ids) {
        return fhirService.searchResourcesById(resourceClass, ids);
    }

    /**
     * Returns the type of domain objects created by this factory.
     *
     * @return The type of domain objects created by this factory.
     */
    @Override
    public Class<T> getDomainClass() {
        return domainClass;
    }

}
