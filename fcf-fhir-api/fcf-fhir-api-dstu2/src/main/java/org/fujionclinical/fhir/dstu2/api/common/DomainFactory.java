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
package org.fujionclinical.fhir.dstu2.api.common;

import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.fujion.common.MiscUtil;
import org.fujionclinical.api.domain.IDomainFactory;

import java.util.Collections;
import java.util.List;

/**
 * Factory for instantiating serialized domain objects from server.
 */
public class DomainFactory implements IDomainFactory<BaseResource> {

    private static final IDomainFactory<BaseResource> instance = new DomainFactory();
    
    private IGenericClient fhirClient;
    
    public static IDomainFactory<BaseResource> getInstance() {
        return instance;
    }
    
    /**
     * Create a new instance of the domain class.
     */
    @Override
    public <T extends BaseResource> T newObject(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
    
    /**
     * Fetch an instance of the domain class from the data store.
     */
    @Override
    public <T extends BaseResource> T fetchObject(Class<T> clazz, String id) {
        return fhirClient.read().resource(clazz).withId(id).execute();
    }
    
    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    @Override
    public <T extends BaseResource> List<T> fetchObjects(Class<T> clazz, String[] ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }
        
        StringClientParam param = new StringClientParam(BaseResource.SP_RES_ID);
        Bundle results = fhirClient.search().forResource(clazz).where(param.matches().values(ids)).returnBundle(Bundle.class)
                .execute();
        return FhirUtil.getEntries(results, clazz);
    }
    
    /**
     * Returns the alias for the domain class.
     *
     * @param clazz Domain class whose alias is sought.
     * @return The alias for the domain class.
     */
    @Override
    public String getAlias(Class<?> clazz) {
        String pkg = clazz.getPackage().getName();
        return pkg.startsWith("ca.uhn.fhir") || pkg.startsWith("org.hl7.fhir") ? clazz.getSimpleName() : null;
    }
    
    /**
     * Returns the FHIR client.
     *
     * @return FHIR client.
     */
    public IGenericClient getFhirClient() {
        return fhirClient;
    }
    
    /**
     * Sets the FHIR client.
     *
     * @param fhirClient The FHIR client.
     */
    public void setFhirClient(IGenericClient fhirClient) {
        this.fhirClient = fhirClient;
    }
    
}
