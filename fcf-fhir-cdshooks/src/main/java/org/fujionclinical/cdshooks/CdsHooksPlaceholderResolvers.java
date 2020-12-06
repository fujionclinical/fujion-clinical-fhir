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

import org.fujion.common.AbstractRegistry;
import org.fujion.common.RegistryMap;
import org.opencds.hooks.model.request.CdsRequest;

/**
 * Registers placeholder resolvers for prefetch queries.  Automatically registers a resolver for context-based
 * placeholders.
 */
public class CdsHooksPlaceholderResolvers extends AbstractRegistry<String, CdsHooksPlaceholderResolvers.ICdsHooksPlaceholderResolver> {

    public interface ICdsHooksPlaceholderResolver {

        /**
         * @return The parameter type.
         */
        String getType();

        /**
         * Resolves the placeholder.
         *
         * @param placeholder The parameter placeholder to resolve.
         * @param request The CDS Hooks request.
         * @return The resolved value.
         */
        String resolve(String placeholder, CdsRequest request);

    }

    private static final CdsHooksPlaceholderResolvers instance = new CdsHooksPlaceholderResolvers();

    public static CdsHooksPlaceholderResolvers getInstance() {
        return instance;
    }

    @Override
    protected String getKey(ICdsHooksPlaceholderResolver item) {
        return item.getType();
    }

    private CdsHooksPlaceholderResolvers() {
        super(RegistryMap.DuplicateAction.ERROR);

        register(new ICdsHooksPlaceholderResolver() {
            @Override
            public String getType() {
                return "context";
            }

            @Override
            public String resolve(String placeholder, CdsRequest request) {
                return request.getContext().get(placeholder, String.class);
            }
        });
    }
}
