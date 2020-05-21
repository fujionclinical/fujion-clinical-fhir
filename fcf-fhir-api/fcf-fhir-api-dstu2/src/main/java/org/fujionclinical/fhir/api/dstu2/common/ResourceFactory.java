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
package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import org.fujionclinical.api.model.IDomainObject;
import org.fujionclinical.fhir.api.common.core.BaseResourceFactory;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for instantiating serialized domain objects from server.
 */
public abstract class ResourceFactory<T extends IDomainObject, R extends IBaseResource> extends BaseResourceFactory<T, R> {

    protected ResourceFactory(
            IGenericClient fhirClient,
            Class<T> wrapperClass,
            Class<R> resourceClass) {
        super(fhirClient, wrapperClass, resourceClass);
    }

    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    @Override
    public List<T> fetchObjects(String[] ids) {
        IQuery<IBaseBundle> result = query(ids);

        if (result == null) {
            return Collections.emptyList();
        }

        Bundle bundle = result.returnBundle(Bundle.class).execute();
        return FhirUtil.getEntries(bundle, resourceClass).stream()
                .map(this::wrapResource)
                .collect(Collectors.toList());
    }

}