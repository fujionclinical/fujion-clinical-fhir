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
package org.fujionclinical.fhir.common.smart;

import org.fujion.common.WeakList;
import org.fujionclinical.api.event.IEventManager;
import org.fujionclinical.api.event.IGenericEvent;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract base class for SMART context adapters. This class defines most of the logic required to
 * convey CareWeb-based context changes to SMART subscribers of the equivalent context scope.
 * Subclasses must implement the updateContext method (see SmartContextUser subclass as an example)
 * and should be configured as singleton beans in the desktop application context via Spring IOC.
 * Naming of the singleton beans must follow the convention "smart.context.[context scope]" where
 * [context scope] is the name of the SMART context as passed to the constructor (e.g.,
 * "smart.context.user").
 */
public abstract class SmartContextBase implements IGenericEvent<Object>, ISmartContext {

    public static class ContextMap extends HashMap<String, String> {

        private static final long serialVersionUID = 1L;

        protected ContextMap() {
            super();
        }

        protected ContextMap(ContextMap contextMap) {
            super(contextMap);
        }
    }

    public static final String SMART_CONTEXT_EVENT = "SMART.CONTEXT.";

    private IEventManager eventManager;

    private final String contextScope;

    private final String contextEvent;

    private final ContextMap context = new ContextMap();

    private final List<ISmartContextSubscriber> subscribers = new WeakList<>();

    private final IGenericEvent<String> refreshListener = new IGenericEvent<String>() {

        @Override
        public void eventCallback(String eventName, String eventData) {
            notifySubscribers();
        }

    };

    /**
     * Main constructor.
     *
     * @param contextScope This is the name of the SMART context scope (e.g., "user").
     * @param contextEvent This is the name of the corresponding CareWeb context change notification
     *            event (e.g., "CONTEXT.CHANGED.User").
     */
    public SmartContextBase(String contextScope, String contextEvent) {
        this.contextScope = contextScope;
        this.contextEvent = contextEvent;
    }

    /**
     * Called by Spring IOC to initialize the class. The default implementation subscribes to the
     * appropriate context change notification event and updates the SMART context with the current
     * context state.
     */
    public void init() {
        eventManager.subscribe(contextEvent, this);
        eventManager.subscribe("VIEW.REFRESH", refreshListener);
        updateContext(context);
    }

    /**
     * Called by Spring IOC to tear down the class. It simply unsubscribes from context change
     * notifications.
     */
    public void destroy() {
        eventManager.unsubscribe(contextEvent, this);
        eventManager.unsubscribe("VIEW.REFRESH", refreshListener);
    }

    /**
     * Callback for context change notifications. Resets the SMART context to reflect the changes
     * and notifies any subscribing SMART containers of the change.
     */
    @Override
    public void eventCallback(String eventName, Object eventData) {
        context.clear();
        updateContext(context);
        notifySubscribers();
    }

    /**
     * Used by Spring IOC to set the active event manager.
     *
     * @param eventManager The event manager.
     */
    public void setEventManager(IEventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Notifies each subscriber of a SMART context change.
     */
    protected void notifySubscribers() {
        for (ISmartContextSubscriber subscriber : subscribers) {
            notifySubscriber(subscriber);
        }
    }

    /**
     * Notifies a single subscriber of a SMART context change. Note that a copy of the SMART context
     * is passed to the subscriber as the subscriber may choose to augment the context in some way.
     *
     * @param subscriber The context subscriber.
     */
    protected void notifySubscriber(ISmartContextSubscriber subscriber) {
        if (subscriber != null) {
            subscriber.updateContext(contextScope, new ContextMap(context));
        }
    }

    /**
     * To be implemented by each subclass to update the SMART context to reflect the new context
     * state.
     *
     * @param context Context map to receive the new context state.
     */
    protected abstract void updateContext(ContextMap context);

    /**
     * Returns the name of the SMART context scope.
     *
     * @return The context scope.
     */
    @Override
    public String getContextScope() {
        return contextScope;
    }

    /**
     * ISmartContext.subscribe method allows a SMART container to subscribe to context changes for
     * the target scope as declared by its hosted application. subscriber The subscriber.
     */
    @Override
    public void subscribe(ISmartContextSubscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
            notifySubscriber(subscriber);
        }
    }

    /**
     * ISmartContext.unsubscribe method allows a SMART container to unsubscribe from context changes
     * for the target scope as declared by its hosted application.
     */
    @Override
    public void unsubscribe(ISmartContextSubscriber subscriber) {
        subscribers.remove(subscriber);
    }
}
