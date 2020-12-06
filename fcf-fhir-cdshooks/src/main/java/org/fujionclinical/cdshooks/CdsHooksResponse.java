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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opencds.hooks.model.discovery.Service;
import org.opencds.hooks.model.response.CdsResponse;

/**
 * Subclasses CdsResponse to add other attributes.
 */
public class CdsHooksResponse extends CdsResponse {

    private final Service service;

    private Exception exception;

    CdsHooksResponse(Service service) {
        super();
        this.service = service;
    }

    CdsHooksResponse(Service service, CdsResponse response) {
        this(service);
        setCards(response.getCards());
    }

    public boolean hasException() {
        return exception != null;
    }

    @JsonIgnore
    public Exception getException() {
        return exception;
    }

    public String getError() {
        return exception == null ? null : exception.getMessage();
    }

    protected void setException(Exception exception) {
        this.exception = exception;
    }

    @JsonIgnore
    public Service getService() {
        return service;
    }
}
