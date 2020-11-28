package org.fujionclinical.fhir.api.common.client;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import edu.utah.kmm.model.cool.mediator.fhir.core.AbstractFhirDataSource;
import org.fujionclinical.fhir.security.common.IAuthInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Base class for all FHIR data sources.  FHIR connection settings are taken from a configurator using the
 * data source ID as the qualifier for property names.
 */
public abstract class AbstractFhirDataSourceFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    abstract protected AbstractFhirDataSource<?, ?> create(
            String dataSourceId,
            IGenericClient client);

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
        return create(dataSourceId, client);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
