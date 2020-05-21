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

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.fujion.common.MiscUtil;
import org.fujionclinical.api.model.IDomainFactory;
import org.fujionclinical.api.model.IDomainObject;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Factory for instantiating serialized domain objects from server.
 */
public abstract class BaseResourceFactory<T extends IDomainObject, R extends IBaseResource> implements IDomainFactory<T> {

    private final IGenericClient fhirClient;

    protected final Class<R> resourceClass;

    protected final Class<T> wrapperClass;

    protected BaseResourceFactory(
            IGenericClient fhirClient,
            Class<T> wrapperClass,
            Class<R> resourceClass) {
        this.fhirClient = fhirClient;
        this.wrapperClass = wrapperClass;
        this.resourceClass = resourceClass;
    }

    /**
     * Create a new instance of the domain class.
     */
    @Override
    public T create(String id) {
        try {
            R instance = resourceClass.getDeclaredConstructor().newInstance();
            instance.setId(id);
            return wrapResource(instance);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    /**
     * Fetch an instance of the domain class from the data store.
     */
    @Override
    public T fetchObject(String id) {
        return wrapResource(fhirClient.read().resource(resourceClass).withId(id).execute());
    }

    protected abstract T wrapResource(R resource);

    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    protected IQuery<IBaseBundle> query(String[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }

        StringClientParam param = new StringClientParam("_id");
        return fhirClient.search().forResource(resourceClass).where(param.matches().values(ids));
    }

    /**
     * Returns the type of domain objects created by this factory.
     *
      * @return The type of domain objects created by this factory.
     */
    @Override
    public Class<T> getDomainClass() {
        return wrapperClass;
    }

}
