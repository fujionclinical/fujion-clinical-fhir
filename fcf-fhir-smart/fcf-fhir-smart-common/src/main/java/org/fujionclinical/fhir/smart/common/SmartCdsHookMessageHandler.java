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

import org.fujionclinical.api.event.EventManager;
import org.fujionclinical.api.event.IEventManager;
import org.fujionclinical.api.event.IEventSubscriber;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartCdsHookMessageHandler extends SmartMessageHandler {

    private final Map<String, Object> cdsHookResponses = new HashMap<>();

    private final Map<String, List<Map<String, Object>>> pendingResponses = new HashMap<>();

    private final IEventManager eventManager = EventManager.getInstance();

    private final IEventSubscriber cdsHookResponseHandler = ((eventName, cdsHookResponse) -> {
        String pcs[] = eventName.split("\\.");

        if (pcs.length > 3) {
            String cdsHook = pcs[3];

            if (cdsHookResponse == null) {
                cdsHookResponses.remove(cdsHook);
                pendingResponses.remove(cdsHook);
            } else {
                processPendingResponses(cdsHook, cdsHookResponse);
                cdsHookResponses.put(cdsHook, cdsHookResponse);
            }

        }
    });

    private final IEventSubscriber cdsHookInitHandler = ((eventName, eventData) -> {
        cdsHookResponses.clear();
        pendingResponses.clear();
    });

    public SmartCdsHookMessageHandler() {
        super("cdshook.listen");
        eventManager.subscribe("cdshook.trigger", cdsHookInitHandler);
        eventManager.subscribe("cdshook.response", cdsHookResponseHandler);
    }

    @Override
    protected Map<String, Object> handleRequest(Map<String, Object> request) {
        String cdsHook = getFromPayload(request, "cdshook");

        if (cdsHook == null) {
            return null;
        }

        Object cdsHookResponse = cdsHookResponses.get(cdsHook);
        Map<String, Object> payload = new HashMap<>();
        payload.put("cdshook", cdsHook);
        payload.put("response", cdsHookResponse);

        Map<String, Object> response = createResponse(request, payload, HttpStatus.OK);

        if (cdsHookResponse != null) {
            return response;
        }

        addPendingResponse(cdsHook, response);
        return null;
    }

    private void processPendingResponses(
            String cdsHook,
            Object cdsHookResponse) {
        List<Map<String, Object>> pending = pendingResponses.remove(cdsHook);

        if (pending != null) {
            for (Map<String, Object> response : pending) {
                Map<String, Object> payload = getPayload(response);
                payload.put("response", cdsHookResponse);
                sendResponse(response);
            }
        }
    }

    private void addPendingResponse(
            String cdsHook,
            Map<String, Object> response) {
        List<Map<String, Object>> pending = pendingResponses.get(cdsHook);

        if (pending == null) {
            pendingResponses.put(cdsHook, pending = new ArrayList<Map<String, Object>>());
        }

        pending.add(response);
    }

}
