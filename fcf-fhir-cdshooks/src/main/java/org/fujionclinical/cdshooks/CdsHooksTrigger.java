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

import edu.utah.kmm.model.cool.mediator.datasource.DataSources;
import edu.utah.kmm.model.cool.mediator.fhir.common.AbstractFhirDataSource;
import org.fujion.client.ExecutionContext;
import org.fujion.common.Logger;
import org.fujionclinical.api.event.EventManager;
import org.fujionclinical.api.event.IEventManager;
import org.fujionclinical.api.user.User;
import org.fujionclinical.api.user.UserContext;
import org.fujionclinical.cdshooks.CdsHooksClient.InvocationRequest;
import org.fujionclinical.cdshooks.CdsHooksPreparedRequest.CdsHooksContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for defining a trigger for a CDS Hooks type.
 */
@SuppressWarnings("rawtypes")
public abstract class CdsHooksTrigger {

    private static final Logger log = Logger.create(CdsHooksTrigger.class);

    private final String hookType;

    private final AbstractFhirDataSource dataSource;

    private final String pid;

    private final String triggerEventName;

    private final IEventManager eventManager = EventManager.getInstance();

    private final List<InvocationRequest> invocationRequests = new ArrayList<>();

    public CdsHooksTrigger(
            String dataSourceId,
            String hookType) {
        this.dataSource = (AbstractFhirDataSource) DataSources.get(dataSourceId);
        this.hookType = hookType;
        this.pid = ExecutionContext.getPage().getId();
        triggerEventName = CdsHooksUtil.makeEventName("trigger", hookType);
        initTriggerLogic();
    }

    protected abstract void initTriggerLogic();

    /**
     * Creates a CDS Hooks context containing the id of the requesting user.
     *
     * @return New CDS Hooks context.
     */
    protected CdsHooksContext createContext() {
        CdsHooksContext context = new CdsHooksContext();
        User user = UserContext.getActiveUser();
        context.put("userId", user == null ? null : user.getId());
        return context;
    }

    /**
     * Invoked by trigger logic.
     *
     * @param context The CDS Hooks context.
     */
    protected void onTrigger(CdsHooksContext context) {
        log.info(() -> "CDS Hooks type " + hookType + " was triggered.");
        stopAllRequests();
        ExecutionContext.invoke(pid, () -> eventManager.fireLocalEvent(triggerEventName, null));
        invocationRequests.addAll(CdsHooksClientRegistry.getInstance().createInvocationRequests(dataSource.getClient(), hookType, context, this::processResponses));
    }

    private synchronized void processResponses(InvocationRequest invocationRequest) {
        invocationRequests.remove(invocationRequest);

        if (!invocationRequest.isAborted()) {
            ExecutionContext.invoke(pid, () -> {
                for (CdsHooksResponse response : invocationRequest.getResponses()) {
                    eventManager.fireLocalEvent(CdsHooksUtil.makeEventName("response", hookType, response.getService().getId()), response);
                }
            });
        }
    }

    private synchronized void stopAllRequests() {
        for (InvocationRequest invocationRequest: invocationRequests) {
            invocationRequest.abort();
        }

        invocationRequests.clear();
    }
}
