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
import org.fujionclinical.api.model.core.IDomainDAO;
import org.fujionclinical.api.model.core.IDomainObject;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.query.QueryExpressionTuple;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.Collections;
import java.util.List;

/**
 * DAO for FHIR resources.
 */
public abstract class AbstractResourceDAO<T extends IDomainObject, R extends IBaseResource> implements IDomainDAO<T> {

    protected final Class<R> resourceClass;

    protected final Class<T> domainClass;

    protected final AbstractFhirService fhirService;

    protected final IWrapperTransform<T, R> transform;

    protected AbstractResourceDAO(
            AbstractFhirService fhirService,
            Class<T> domainClass,
            Class<R> resourceClass,
            IWrapperTransform<T, R> transform) {
        this.fhirService = fhirService;
        this.domainClass = domainClass;
        this.resourceClass = resourceClass;
        this.transform = transform;
    }

    /**
     * Fetch an instance of the domain class from the data store.
     */
    @Override
    public T read(String id) {
        return transform.wrap((R) fhirService.getResource(resourceClass, id));
    }

    protected abstract List<T> execute(IQuery<IBaseBundle> query);

    protected String toQueryString(List<QueryExpressionTuple> tuples) {
        return QueryBuilder.buildQueryString(domainClass, tuples);
    }

    /**
     * Performs a query, returning a list of matching domain objects.
     *
     * @param tuples A list of query expression tuples.
     * @return A list of matching domain objects.
     */
    protected IQuery<IBaseBundle> query(List<QueryExpressionTuple> tuples) {
        return fhirService.searchResources(resourceClass, toQueryString(tuples));
    }

    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    @Override
    public List<T> read(String... ids) {
        IQuery<IBaseBundle> result = fhirService.searchResourcesById(resourceClass, ids);
        return result == null ? Collections.emptyList() : execute(result);
    }

    /**
     * Performs a query, returning a list of matching domain objects.
     *
     * @param tuples A list of query expression tuples
     * @return A list of matching domain objects.
     */
    @Override
    public List<T> search(List<QueryExpressionTuple> tuples) {
        return execute(this.query(tuples));
    }

    @Override
    public T create(T template) {
        return transform.wrap((R) fhirService.createResource(transform.unwrap(template)));
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
