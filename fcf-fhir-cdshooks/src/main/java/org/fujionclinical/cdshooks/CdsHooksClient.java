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
package org.fujionclinical.cdshooks;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.fujion.common.Logger;
import org.fujion.thread.ThreadUtil;
import org.fujionclinical.cdshooks.CdsHooksPreparedRequest.CdsHooksContext;
import org.opencds.hooks.model.discovery.Service;
import org.opencds.hooks.model.response.CdsResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;

/**
 * Client for accessing REST services for a specified CDS Hooks endpoint. The client not only
 * retrieves and exposes the services catalog for the endpoint, but provides support for invoking a
 * service.
 */
public class CdsHooksClient {

    /**
     * An invocation request for this hook type.
     */
    public class InvocationRequest extends Thread {

        private final IGenericClient fhirClient;

        private final String hookType;

        private final CdsHooksContext context;

        private final Consumer<InvocationRequest> callback;

        private final List<CdsHooksResponse> responses = new ArrayList<>();

        private boolean aborted;

        private InvocationRequest(IGenericClient fhirClient, String hookType, CdsHooksContext context, Consumer<InvocationRequest> callback) {
            super("CDS Hooks Invocation for type " + hookType);
            this.fhirClient = fhirClient;
            this.hookType = hookType;
            this.context = context;
            this.callback = callback;
        }

        public String getHookType() {
            return hookType;
        }

        public void abort() {
            aborted = true;
        }

        public boolean isAborted() {
            return aborted;
        }

        public List<CdsHooksResponse> getResponses() {
            return Collections.unmodifiableList(responses);
        }

        @Override
        public void run() {
            for (Service service : catalog.getServices(hookType)) {
                if (aborted) {
                    responses.clear();
                    break;
                }

                CdsHooksPreparedRequest preparedRequest = prepareRequest(fhirClient, service, context);
                CdsHooksResponse response = invoke(preparedRequest);

                if (response != null) {
                    responses.add(response);
                }
            }

            if (callback != null) {
                callback.accept(this);
            }
        }
    }

    private static final Logger log = Logger.create(CdsHooksClient.class);

    private static final int MAX_RETRIES = 5;

    private static final int RETRY_INTERVAL = 10;

    private final RestTemplate restTemplate;
    
    private final String discoveryEndpoint;

    private final List<InvocationRequest> pendingRequests = new ArrayList<>();

    private boolean inactive;

    private volatile CdsHooksCatalog catalog;

    private int retries = MAX_RETRIES;

    private final ClientHttpRequestInterceptor contentTypeInterceptor = (request, body, execution) -> {
        HttpHeaders headers = request.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return execution.execute(request, body);
    };
    
    /**
     * Create and initialize a client for the given endpoint.
     *
     * @param discoveryEndpoint The URL of the discovery endpoint.
     * @exception Exception unspecified exception.
     */
    public CdsHooksClient(String discoveryEndpoint) throws Exception {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            restTemplate = new RestTemplate(requestFactory);
            restTemplate.setInterceptors(Collections.singletonList(contentTypeInterceptor));

            if (!discoveryEndpoint.endsWith("/cds-services")) {
                discoveryEndpoint += "/cds-services";
            }

            this.discoveryEndpoint = StringUtils.trimToNull(discoveryEndpoint);
            loadCatalog();
    }

    /**
     * Load the catalog in a worker thread.
     */
    private void loadCatalog() {
        try {
            log.info("Attempting to retrieve CDS Hooks catalog from " + discoveryEndpoint + "...");
            ThreadUtil.getApplicationThreadPool().execute(createThread());
        } catch (Exception e) {
            log.error("Error attempting to retrieve CDS Hooks catalog from " + discoveryEndpoint, e);
            retry();
        }
    }

    /**
     * Creates worker thread to retrieve catalog.
     *
     * @return The worker thread.
     */
    private Thread createThread() {
        return new Thread(this::_loadCatalog, "CDS Hooks Catalog Retrieval"
        );
    }

    /**
     * Retry attempt to load catalog after retry interval has expired.
     */
    private void retry() {
        if (catalog == null)      {
            if (--retries < 0) {
                inactive = true;
                pendingRequests.clear();
                log.error("Maximum retries exceeded while attempting to load catalog for endpoint " + discoveryEndpoint
                    + ".\nThis service will be unavailabe.");
            } else {
                try {
                    sleep(RETRY_INTERVAL * 1000);
                    _loadCatalog();
                } catch (InterruptedException e) {
                    log.warn("Thread for loading CDS Hooks catalog for " + discoveryEndpoint + "has been interrupted"
                        + ".\nThis service will be unavailable.");
                }
            }
        }
    }

    /**
     * Loads the CDS Hooks catalog for this endpoint.
     */
    private void _loadCatalog() {
        if (discoveryEndpoint != null) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(discoveryEndpoint, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    String body = response.getBody();
                    this.catalog = CdsHooksUtil.GSON.fromJson(body, CdsHooksCatalog.class);
                    startPendingRequests();
                    return;
                }
            } catch (Exception e) {
                log.warn("Failed to load CDS catalog for " + discoveryEndpoint
                    + ".\nRetries remaining: " + retries + ".");
            }

            retry();
        }
    }

    /**
     * Returns the CDS Hooks catalog.
     *
     * @return The CDS Hooks catalog (possibly null).
     */
    public CdsHooksCatalog getCatalog() {
        return catalog;
    }

    /**
     * Returns the URL of the discovery endpoint.
     *
     * @return The URL of the discovery endpoint.
     */
    public String getDiscoveryEndpoint() {
        return discoveryEndpoint;
    }

    /**
     * Creates a request to invoke all services of the specified type for this endpoint.  If the catalog has not
     * yet been initialized, the request is queued.  Otherwise, it is started in a worker thread.
     *
     * @param fhirClient The FHIR client.
     * @param hookType The hook type.
     * @param context The hook context.
     * @param callback The function to call upon completion.
     * @return The newly created invocation request.
     */
    public InvocationRequest createInvocationRequest(IGenericClient fhirClient, String hookType, CdsHooksContext context, Consumer<InvocationRequest> callback) {
        if (inactive) {
            return null;
        }

        InvocationRequest invocationRequest = new InvocationRequest(fhirClient, hookType, context, callback);

        if (catalog == null) {
            pendingRequests.add(invocationRequest);
        } else {
            ThreadUtil.execute(invocationRequest);
        }

        return invocationRequest;
    }

    /**
     * Starts any pending requests (after catalog has been loaded).
     */
    private synchronized void startPendingRequests() {
        for (InvocationRequest invocationRequest : pendingRequests) {
            if (!invocationRequest.aborted) {
                ThreadUtil.execute(invocationRequest);
            }
        }

        pendingRequests.clear();
    }

    /**
     * Returns the service with the specified id.
     *
     * @param id The service id.
     * @return The service with the specified id.
     * @exception IllegalArgumentException Thrown if no service with the specified id exists.
     */
    public Service getService(String id) {
        Service service = catalog.getService(id);
        Assert.notNull(service, () -> "No service with id '" + id + "' found");
        return service;
    }
    
    /**
     * Generates a prepared CDS Hooks request.
     *
     * @param fhirClient The FHIR client.
     * @param service The hook service.
     * @param context The hook context.
     * @return A prepared CDS Hooks request.
     */
    private CdsHooksPreparedRequest prepareRequest(IGenericClient fhirClient, Service service, CdsHooksContext context) {
        return new CdsHooksPreparedRequest(fhirClient, service, context);
    }

    /**
     * Invokes a prepared request.  Fires an event with the response as its payload.  The event name
     * is formatted as: cdshook.response.[hook type].[service id]
     *
     * @param preparedRequest A prepared request.
     * @return The invocation response (never null).
     */
    private CdsHooksResponse invoke(CdsHooksPreparedRequest preparedRequest) {
        CdsHooksResponse response;
        Service service = preparedRequest.getService();
        String serviceId = service.getId();
        String hookId = service.getHook();

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    discoveryEndpoint + "/" + serviceId, preparedRequest.getBody(), String.class);
            CdsResponse resp = CdsHooksUtil.GSON.fromJson(responseEntity.getBody(), CdsResponse.class);
            response = new CdsHooksResponse(service, resp);
        } catch (Exception e) {
            log.error("Error invoking CDS hook " + hookId + " @ " + serviceId, e);
            response = new CdsHooksResponse(service);
            response.setException(e);
        }

        return response;
    }
    
}
