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
import edu.utah.kmm.cool.transform.ModelTransform;
import org.fujionclinical.api.model.core.IDomainType;
import org.fujionclinical.api.model.dao.IDomainDAO;
import org.fujionclinical.api.query.expression.ExpressionTuple;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.Collections;
import java.util.List;

/**
 * DAO for FHIR resources.
 */
public abstract class AbstractResourceDAO<L extends IDomainType, N extends IBaseResource> implements IDomainDAO<L> {

    protected final Class<N> nativeType;

    protected final Class<L> logicalType;

    protected final AbstractFhirService fhirService;

    protected final ModelTransform<L, N> transform;

    protected AbstractResourceDAO(
            AbstractFhirService fhirService,
            Class<L> logicalType,
            Class<N> nativeType,
            ModelTransform<L, N> transform) {
        this.fhirService = fhirService;
        this.logicalType = logicalType;
        this.nativeType = nativeType;
        this.transform = transform;
    }

    /**
     * Fetch an instance of the domain class from the data store.
     */
    @Override
    public L read(String id) {
        return transform.toLogicalModel((N) fhirService.getResource(nativeType, id));
    }

    protected abstract List<L> execute(IQuery<IBaseBundle> query);

    protected String toQueryString(List<ExpressionTuple> tuples) {
        return QueryBuilder.buildQueryString(logicalType, tuples);
    }

    /**
     * Performs a query, returning a list of matching domain objects.
     *
     * @param tuples A list of query expression tuples.
     * @return A list of matching domain objects.
     */
    protected IQuery<IBaseBundle> query(List<ExpressionTuple> tuples) {
        return fhirService.searchResources(nativeType, toQueryString(tuples));
    }

    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    @Override
    public List<L> read(String... ids) {
        IQuery<IBaseBundle> result = fhirService.searchResourcesById(nativeType, ids);
        return result == null ? Collections.emptyList() : execute(result);
    }

    /**
     * Performs a query, returning a list of matching domain objects.
     *
     * @param tuples A list of query expression tuples
     * @return A list of matching domain objects.
     */
    @Override
    public List<L> search(List<ExpressionTuple> tuples) {
        return execute(this.query(tuples));
    }

    @Override
    public L create(L template) {
        return transform.toLogicalModel((N) fhirService.createResource(transform.fromLogicalModel(template)));
    }

    /**
     * Returns the type of domain objects created by this factory.
     *
     * @return The type of domain objects created by this factory.
     */
    @Override
    public Class<L> getDomainType() {
        return logicalType;
    }

}
