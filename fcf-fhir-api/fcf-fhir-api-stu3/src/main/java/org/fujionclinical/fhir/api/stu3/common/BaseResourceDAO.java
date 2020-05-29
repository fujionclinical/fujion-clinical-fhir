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
package org.fujionclinical.fhir.api.stu3.common;

import ca.uhn.fhir.rest.gclient.IQuery;
import org.fujionclinical.api.model.IDomainObject;
import org.fujionclinical.fhir.api.common.core.AbstractFhirService;
import org.fujionclinical.fhir.api.common.core.AbstractResourceDAO;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.dstu3.model.Bundle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO for STU3 FHIR resources.
 */
public abstract class BaseResourceDAO<T extends IDomainObject, R extends IBaseResource> extends AbstractResourceDAO<T, R> {

    protected BaseResourceDAO(
            AbstractFhirService fhirService,
            Class<T> wrapperClass,
            Class<R> resourceClass) {
        super(fhirService, wrapperClass, resourceClass);
    }

    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    @Override
    public List<T> read(String... ids) {
        IQuery<IBaseBundle> result = query(ids);

        if (result == null) {
            return Collections.emptyList();
        }

        Bundle bundle = result.returnBundle(Bundle.class).execute();
        return FhirUtilStu3.getEntries(bundle, resourceClass).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

}
