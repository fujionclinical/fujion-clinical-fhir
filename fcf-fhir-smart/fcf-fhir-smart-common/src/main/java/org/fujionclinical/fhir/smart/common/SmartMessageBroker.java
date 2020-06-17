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
package org.fujionclinical.fhir.smart.common;

import org.fujion.component.BaseComponent;
import org.fujion.event.Event;
import org.fujion.event.IEventListener;
import org.fujionclinical.api.event.EventManager;
import org.fujionclinical.api.event.IEventManager;
import org.fujionclinical.api.event.IEventSubscriber;
import org.fujionclinical.api.spring.SpringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Support for bidirectional SMART messaging.
 */
public class SmartMessageBroker {

    private static class PendingResponse {

        final long expiration;

        final BaseComponent container;

        PendingResponse(BaseComponent container) {
            this.container = container;
            this.expiration = System.currentTimeMillis() + TIME_TO_LIVE;
        }

    }

    // Event type for a request from a SMART app.
    public static final String EVENT_REQUEST = "smart_request";

    // Event type for responding to a request from a SMART app.
    public static final String EVENT_RESPONSE = "smart_response";

    // How long before a request will be considered abandoned.
    private static final long TIME_TO_LIVE = 120 * 1000;

    private final IEventManager eventManager = EventManager.getInstance();

    private final Map<String, PendingResponse> pendingResponses = Collections.synchronizedMap(new LinkedHashMap<>());

    private final IEventListener requestListener = (event) -> {
        handleRequest(event);
    };

    private final IEventSubscriber<Map<String, Object>> responseListener = (eventName, eventData) -> {
        handleResponse(eventData);
    };

    public static SmartMessageBroker getInstance() {
        return SpringUtil.getBean("smartMessageBroker", SmartMessageBroker.class);
    }

    public SmartMessageBroker() {
        eventManager.subscribe(EVENT_RESPONSE, responseListener);
    }

    public void registerContainer(BaseComponent container) {
        container.addEventListener(EVENT_REQUEST, requestListener);
    }

    public void unregisterContainer(BaseComponent container) {
        container.removeEventListener(EVENT_REQUEST, requestListener);

        synchronized (pendingResponses) {
            Iterator<Map.Entry<String, PendingResponse>> iter = pendingResponses.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, PendingResponse> entry = iter.next();

                if (entry.getValue().container == container) {
                    iter.remove();
                }
            }
        }
    }

    private void handleRequest(Event event) {
        prunePendingResponses();
        Map<String, Object> request = (Map) event.getData();
        String messageId = (String) request.get("messageId");
        Assert.notNull(messageId, "Cannot dispatch SMART request without a message id");
        pendingResponses.put(messageId, new PendingResponse(event.getTarget()));
        eventManager.fireLocalEvent(EVENT_REQUEST, request);
    }

    private void handleResponse(Map<String, Object> response) {
        String messageId = (String) response.get("responseToMessageId");
        Assert.notNull(messageId, "Cannot dispatch SMART response without a message id");
        PendingResponse pendingResponse = pendingResponses.remove(messageId);

        if (pendingResponse != null) {
            if (!pendingResponse.container.isDead()) {
                response.put("messageId", UUID.randomUUID().toString());
                pendingResponse.container.fireEventToClient(EVENT_RESPONSE, response);
            }
        }

        prunePendingResponses();
    }

    private void prunePendingResponses() {
        long currentTime = System.currentTimeMillis();

        synchronized (pendingResponses) {
            Iterator<Map.Entry<String, PendingResponse>> iter = pendingResponses.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, PendingResponse> entry = iter.next();

                if (currentTime >= entry.getValue().expiration) {
                    iter.remove();
                    String messageId = entry.getKey();
                    Map<String, Object> response = new HashMap<>();
                    response.put("responseToMessageId", messageId);
                    response.put("payload", Collections.singletonMap("status", HttpStatus.REQUEST_TIMEOUT));
                    handleResponse(response);
                } else {
                    break;
                }
            }
        }
    }


}
