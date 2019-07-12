package org.fujionclinical.fhir.smart.common;

import org.fujionclinical.api.event.EventManager;
import org.fujionclinical.api.event.IEventManager;
import org.fujionclinical.api.event.IGenericEvent;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class SmartCdsHookMessageHandler extends SmartMessageHandler {

    private final Map<String, Object> cdsHookResponses = new HashMap<>();

    private final IEventManager eventManager = EventManager.getInstance();

    private final IGenericEvent cdsHookResponseHandler = ((eventName, eventData) -> {
        String pcs[] = eventName.split("\\.");

        if (pcs.length > 3) {
            cdsHookResponses.put(pcs[3], eventData);
        }
    });

    public SmartCdsHookMessageHandler() {
        super("cdshook.response");
        eventManager.subscribe("cdshook.response", cdsHookResponseHandler);
    }

    @Override
    protected Map<String, Object> handleRequest(Map<String, Object> request) {
        String cdsHook = getFromPayload(request, "cdshook");

        if (cdsHook != null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("cdshook", cdsHook);
            Object response = cdsHookResponses.get(cdsHook);

            if (response == null) {
                return createResponse(payload, HttpStatus.NOT_FOUND);
            } else {
                payload.put("response", response);
                return createResponse(payload, HttpStatus.OK);
            }
        }

        return null;
    }
}
