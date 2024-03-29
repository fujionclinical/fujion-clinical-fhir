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
package org.fujionclinical.fhir.api.common.client;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import org.coolmodel.mediator.fhir.common.AbstractFhirDataSource;
import org.coolmodel.mediator.fhir.common.FhirUtils;
import org.fujionclinical.fhir.security.common.IAuthInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *  Factory for creating FHIR data sources.  FHIR connection settings are taken from a configurator using the
 * data source ID as the qualifier for property names.
 */
public class FhirDataSourceFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public AbstractFhirDataSource<?, ?> create(String dataSourceId) {
        FhirConfigurator config = new FhirConfigurator(dataSourceId);
        config.setApplicationContext(applicationContext);
        FhirContext fhirContext = new FhirContext(config);
        IGenericClient client = fhirContext.newRestfulGenericClient(config.getServerBase());

        if (client instanceof GenericClient) {
            ((GenericClient) client).setDontValidateConformance(!config.isValidateConformance());
        }

        IAuthInterceptor authInterceptor = config.getAuthInterceptor();

        if (authInterceptor != null) {
            client.registerInterceptor(authInterceptor);
        }

        client.setPrettyPrint(config.isPrettyPrint());
        client.setEncoding(config.getEncoding());
        client.setSummary(config.getSummary());
        return FhirUtils.createDataSource(dataSourceId, client);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
