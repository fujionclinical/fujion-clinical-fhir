package org.fujionclinical.fhir.api.r4.common;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import edu.utah.kmm.model.cool.mediator.fhir.r4.common.FhirDataSource;
import org.fujionclinical.fhir.api.common.client.AbstractFhirDataSourceFactory;

/**
 * Factory for R4 data source.
 */
public class FhirDataSourceFactory extends AbstractFhirDataSourceFactory<FhirDataSource> {

    @Override
    public FhirDataSource create(String dataSourceId) {
        return super.create(dataSourceId);
    }

    @Override
    protected FhirDataSource create(
            String dataSourceId,
            IGenericClient client) {
        return new FhirDataSource(dataSourceId, client);
    }

}
