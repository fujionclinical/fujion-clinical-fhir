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
package org.fujionclinical.fhir.api.r4.common;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.coolmodel.mediator.fhir.r4.common.R4DataSource;
import org.fujionclinical.fhir.api.common.client.AbstractFhirDataSourceFactory;

/**
 * Factory for R4 data source.
 */
public class R4DataSourceFactory extends AbstractFhirDataSourceFactory<R4DataSource> {

    @Override
    public R4DataSource create(String dataSourceId) {
        return super.create(dataSourceId);
    }

    @Override
    protected R4DataSource create(
            String dataSourceId,
            IGenericClient client) {
        return new R4DataSource(dataSourceId, client);
    }

}
