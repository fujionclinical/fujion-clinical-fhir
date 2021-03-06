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

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.coolmodel.mediator.fhir.dstu2.common.Dstu2DataSource;
import org.fujionclinical.fhir.api.common.client.AbstractFhirDataSourceFactory;

/**
 * Factory for DSTU2 data source.
 */
public class Dstu2DataSourceFactory extends AbstractFhirDataSourceFactory<Dstu2DataSource> {

    @Override
    public Dstu2DataSource create(String dataSourceId) {
        return super.create(dataSourceId);
    }

    @Override
    protected Dstu2DataSource create(
            String dataSourceId,
            IGenericClient client) {
        return new Dstu2DataSource(dataSourceId, client);
    }

}
