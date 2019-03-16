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
package org.fujionclinical.fhir.common.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.fujionclinical.api.spring.PropertyBasedConfigurator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry of factories for creating authentication interceptors.
 */
public class AuthInterceptorRegistry {

    private static final AuthInterceptorRegistry instance = new AuthInterceptorRegistry();

    private final Map<String, Constructor<?>> factories = new HashMap<>();

    private AuthInterceptorRegistry() {
    }

    public static AuthInterceptorRegistry getInstance() {
        return instance;
    }

    /**
     * Registers a custom authentication interceptor.
     *
     * @param id The unique identifier.
     * @param authInterceptorClass The authentication interceptor class.
     */
    public void register(String id, Class<AbstractAuthInterceptor> authInterceptorClass) {
        Constructor<?> ctor = ConstructorUtils
                .getMatchingAccessibleConstructor(authInterceptorClass, PropertyBasedConfigurator.class);

        if (ctor == null) {
            throw new RuntimeException("No suitable constructor found for " + authInterceptorClass);
        }

        factories.put(id.toLowerCase(), ctor);
    }

    /**
     * Registers custom authentication interceptors.
     *
     * @param interceptors Map of interceptors.
     * @throws ClassNotFoundException If specified class was not found.
     */
    @SuppressWarnings("unchecked")
    public void register(Map<String, String> interceptors) throws ClassNotFoundException {
        for (String id : interceptors.keySet()) {
            Class<?> clazz = Class.forName(interceptors.get(id));
            register(id, (Class<AbstractAuthInterceptor>) clazz);
        }
    }

    /**
     * Returns the authentication interceptor with the specified id, throwing an exception if not
     * found.
     *
     * @param id The unique identifier.
     * @param parentConfigurator The configurator.
     * @return The corresponding authentication interceptor, or null if authType was not specified.
     */
    public IAuthInterceptor create(String id, PropertyBasedConfigurator parentConfigurator) {
        id = StringUtils.trimToNull(id);

        if (id == null) {
            return null;
        }

        Constructor<?> ctor = factories.get(id.toLowerCase());

        if (ctor == null) {
            throw new RuntimeException("The authentication type '" + id + "' is not known");
        }

        try {
            return (IAuthInterceptor) ctor.newInstance(parentConfigurator);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
