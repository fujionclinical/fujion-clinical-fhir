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

import com.google.gson.annotations.SerializedName;
import org.opencds.hooks.model.discovery.Service;

import java.util.*;

/**
 * Represents a catalog of services provided by a given endpoint. It indexes services by hook type
 * and by service id.
 */
public class CdsHooksCatalog implements Iterable<Service> {
    
    @SerializedName("services")
    private final List<Service> services = new ArrayList<>();
    
    private final Map<String, Service> serviceById = new HashMap<>();

    private final Map<String, List<Service>> servicesByType = new HashMap<>();

    private volatile boolean initialized;

    /**
     * Initialize service indexes.
     * 
     * @return The catalog instance (for chaining).
     */
    protected CdsHooksCatalog init() {
        if (!initialized) {
            _init();
        }
        
        return this;
    }

    /**
     * Initialize service indexes (thread safe).
     */
    private synchronized void _init() {
        if (!initialized) {
            for (Service service: services) {
                serviceById.put(service.getId(), service);
                List<Service> svcs = servicesByType.computeIfAbsent(service.getHook(), k -> new ArrayList<>());
                svcs.add(service);
            }

            initialized = true;
        }
    }

    /**
     * Returns true if there are no entries in the catalog.
     *
     * @return True if there are no entries in the catalog.
     */
    public boolean isEmpty() {
        return services.isEmpty();
    }

    /**
     * Returns a list of all services within this catalog.
     *
     * @return A list of all services within this catalog.
     */
    public List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    /**
     * Returns a list of all services of the specified hook type.
     * 
     * @param hookType The hook type.
     * @return A list of all services of the specified hook type.
     */
    public List<Service> getServices(String hookType) {
        return Collections.unmodifiableList(init().servicesByType.get(hookType));
    }

    /**
     * Returns the service with the specified id.
     * 
     * @param id The id of the service sought.
     * @return The service with the specified id, or null if not found.
     */
    public Service getService(String id) {
        return init().serviceById.get(id);
    }

    @Override
    public Iterator<Service> iterator() {
        return getServices().iterator();
    }
    
}
