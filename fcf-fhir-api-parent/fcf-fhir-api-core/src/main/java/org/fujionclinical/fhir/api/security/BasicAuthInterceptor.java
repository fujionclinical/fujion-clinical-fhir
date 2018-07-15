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
package org.fujionclinical.fhir.api.security;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.fujion.common.MiscUtil;
import org.fujionclinical.api.domain.IUser;
import org.fujionclinical.api.security.SecurityUtil;
import org.fujionclinical.api.spring.PropertyBasedConfigurator;
import org.fujionclinical.api.spring.PropertyBasedConfigurator.Param;

/**
 * Authentication interceptor supporting Basic authentication.
 */
public class BasicAuthInterceptor extends AbstractAuthInterceptor {

    @Param(property = "authentication.username")
    private String username;
    
    @Param(property = "authentication.password")
    private String password;
    
    private final String credentials;

    public BasicAuthInterceptor(PropertyBasedConfigurator parentConfigurator) {
        super(parentConfigurator, "Basic");
        credentials = username == null ? null : encode(username, password);
    }

    @Override
    public String getCredentials() {
        if (credentials != null) {
            return credentials;
        }

        IUser user = SecurityUtil.getAuthenticatedUser();
        return user == null ? null : encode(user.getLoginName(), user.getPassword());
    }

    private String encode(String username, String password) {
        try {
            String credentials = username + ":" + password;
            return Base64.encodeBase64String(credentials.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
}
