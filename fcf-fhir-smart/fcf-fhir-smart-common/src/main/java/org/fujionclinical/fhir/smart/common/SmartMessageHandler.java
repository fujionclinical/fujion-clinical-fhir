/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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

import org.fujion.event.IEventListener;
import org.fujionclinical.api.event.EventManager;
import org.fujionclinical.api.event.IEventManager;
import org.fujionclinical.api.event.IGenericEvent;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for implementing handlers for SMART messages.
 */
public abstract class SmartMessageHandler {

    private final IEventManager eventManager = EventManager.getInstance();

    private String messageType;

    private final IGenericEvent<Map<String, Object>> requestHandler = (eventName, request) -> {
        String type = (String) request.get("messageType");

        if (messageType.equals(type)) {
            _handleRequest(request);
        }
    };

    protected SmartMessageHandler(String messageType) {
        this.messageType = messageType;
        this.eventManager.subscribe(SmartMessagingService.EVENT_REQUEST, requestHandler);
    }

    protected abstract Map<String, Object> handleRequest(Map<String, Object> request);

    private void _handleRequest(Map<String, Object> request) {
        Map<String, Object> response = handleRequest(request);

        if (response != null) {
            response.put("responseToMessageId", request.get("messageId"));
            eventManager.fireLocalEvent(SmartMessagingService.EVENT_RESPONSE, response);
        }

    }

    protected Map<String, Object> createResponse(Map<String, Object> payload, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("payload", payload);
        payload.put("status", status.value());
        return response;
    }

    protected Map<String, Object> getPayload(Map<String, Object> source) {
        return (Map<String, Object>) source.get("payload");
    }

    protected String getFromPayload(Map<String, Object> source, String param) {
        Map<String, Object> payload = getPayload(source);
        return payload == null ? null : (String) payload.get(param);
    }
}
