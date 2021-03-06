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

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import org.fujionclinical.api.spring.SimplePropertyAwareConfigurator;
import org.fujionclinical.fhir.security.common.AuthInterceptorRegistry;
import org.fujionclinical.fhir.security.common.IAuthInterceptor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Configurator for all FHIR client-related settings. This is a property-based configurator with a
 * property prefix formatted as: <code>fhir.client[.&lt;qualifier&gt;]</code>. For example, if the
 * qualifier is "terminology_service", the server base property would be specified as
 * <code>fhir.client.terminology_service.server.base</code>. For the default (i.e., null or empty)
 * qualifier it would be <code>fhir.client.server.base</code>.
 */
@SuppressWarnings("UnusedDeclaration")
public class FhirConfigurator extends SimplePropertyAwareConfigurator
        implements IFhirClientConfigurator, IFhirContextConfigurator, ApplicationListener<ContextRefreshedEvent> {

    @Param(property = "version", required = true)
    private FhirVersionEnum version;

    @Param(property = "proxy")
    private String proxy;

    @Param(property = "connection.request.timeout", defaultValue = "10000")
    private int connectionRequestTimeout;

    @Param(property = "connect.timeout", defaultValue = "10000")
    private int connectTimeout;

    @Param(property = "pool.max.per.route", defaultValue = "20")
    private int poolMaxPerRoute;

    @Param(property = "pool.max.total", defaultValue = "20")
    private int poolMaxTotal;

    @Param(property = "socket.timeout", defaultValue = "10000")
    private int socketTimeout;

    @Param(property = "server.validation.mode", defaultValue = "ONCE")
    private ServerValidationModeEnum serverValidationMode;

    @Param(property = "server.base", required = true)
    private String serverBase;

    @Param(property = "authentication.type")
    private String authenticationType;

    @Param(property = "validate.conformance", defaultValue = "false")
    private boolean validateConformance;

    @Param(property = "encoding", defaultValue = "XML")
    private EncodingEnum encoding;

    @Param(property = "prettyprint", defaultValue = "false")
    private boolean prettyPrint;

    @Param(property = "summary")
    private SummaryEnum summary;

    private IAuthInterceptor authInterceptor;

    public FhirConfigurator(String qualifier) {
        super("fhir", qualifier);
    }

    @Override
    public String getServerBase() {
        return serverBase;
    }

    @Override
    public boolean isValidateConformance() {
        return validateConformance;
    }

    @Override
    public EncodingEnum getEncoding() {
        return encoding;
    }

    @Override
    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    @Override
    public SummaryEnum getSummary() {
        return summary;
    }

    @Override
    public IAuthInterceptor getAuthInterceptor() {
        return authInterceptor;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        authInterceptor = AuthInterceptorRegistry.getInstance().create(authenticationType, this);
    }

    @Override
    public FhirVersionEnum getVersion() {
        return version;
    }

    @Override
    public String getProxy() {
        return proxy;
    }

    @Override
    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public ServerValidationModeEnum getServerValidationMode() {
        return serverValidationMode;
    }

    @Override
    public int getSocketTimeout() {
        return socketTimeout;
    }

    @Override
    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    @Override
    public int getPoolMaxPerRoute() {
        return poolMaxPerRoute;
    }

    @Override
    protected String preprocess(
            Param annotation,
            String value) {
        if (value != null && annotation.property().equals("version")) {
            value = value.toUpperCase();

            if (value.equals("STU3")) {
                value = "DSTU3";
            }
        }

        return value;
    }

}
