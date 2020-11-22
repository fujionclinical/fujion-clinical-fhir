package org.fujionclinical.fhir.api.common.client;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import edu.utah.kmm.model.cool.mediator.fhir.core.AbstractFhirDataSource;
import org.fujionclinical.fhir.security.common.IAuthInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractFhirDataSourceFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    abstract protected AbstractFhirDataSource create(
            String dataSourceId,
            IGenericClient client);

    public AbstractFhirDataSource create(String dataSourceId) {
        FhirContextConfigurator contextConfigurator = new FhirContextConfigurator(dataSourceId);
        contextConfigurator.setApplicationContext(applicationContext);
        FhirClientConfigurator clientConfigurator = new FhirClientConfigurator(dataSourceId);
        clientConfigurator.setApplicationContext(applicationContext);
        FhirContext fhirContext = new FhirContext(contextConfigurator);
        IGenericClient client = fhirContext.newRestfulGenericClient(clientConfigurator.getServerBase());

        if (client instanceof GenericClient) {
            ((GenericClient) client).setDontValidateConformance(!clientConfigurator.isValidateConformance());
        }

        IAuthInterceptor authInterceptor = clientConfigurator.getAuthInterceptor();

        if (authInterceptor != null) {
            client.registerInterceptor(authInterceptor);
        }

        client.setPrettyPrint(clientConfigurator.isPrettyPrint());
        client.setEncoding(clientConfigurator.getEncoding());
        client.setSummary(clientConfigurator.getSummary());
        return create(dataSourceId, client);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
