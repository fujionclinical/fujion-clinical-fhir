package org.fujionclinical.fhir.api.stu3.common;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import edu.utah.kmm.model.cool.mediator.fhir.core.AbstractFhirDataSource;
import edu.utah.kmm.model.cool.mediator.fhir.stu3.common.FhirDataSource;
import org.fujionclinical.fhir.api.common.client.AbstractFhirDataSourceFactory;

public class FhirDataSourceFactory extends AbstractFhirDataSourceFactory {

    @Override
    protected AbstractFhirDataSource create(
            String dataSourceId,
            IGenericClient client) {
        return new FhirDataSource(dataSourceId, client);
    }

}
