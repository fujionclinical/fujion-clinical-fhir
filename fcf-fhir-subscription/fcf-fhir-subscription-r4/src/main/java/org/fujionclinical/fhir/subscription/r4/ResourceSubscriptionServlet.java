/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2019 fujionclinical.org
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
package org.fujionclinical.fhir.subscription.r4;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet supporting REST callback for FHIR subscription notifications. GET and POST calls are
 * supported.
 */
public class ResourceSubscriptionServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(ResourceSubscriptionServlet.class);

    private ResourceSubscriptionService service;

    private boolean disabled;
    
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }
    
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }
    
    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse res) {
        // NOP
    }
    
    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse res) {
        // NOP
    }
    
    protected final void processRequest(HttpServletRequest req, HttpServletResponse res) {
        boolean rejected = !initService(req);

        if (!rejected) {
            try {
                String id = req.getPathInfo().substring(1);
                rejected = !service.notifySubscribers(id, IOUtils.toString(req.getReader()));
            } catch (Exception e) {
                rejected = true;
                disableService("notifying subscribers of a resource subscription event", e);
            }
        }

        res.setStatus(rejected ? 410 : 200);
    }
    
    /**
     * Initialize the resource subscription service as necessary.
     *
     * @param req HTTP request for retrieving Spring application context.
     * @return True if initialization was successful.
     */
    private synchronized boolean initService(HttpServletRequest req) {
        if (!disabled && service == null) {
            try {
                ApplicationContext ctx = WebApplicationContextUtils
                        .getRequiredWebApplicationContext(req.getServletContext());
                service = ctx.getBean(ResourceSubscriptionService.class);
                disabled = service.isDisabled();
            } catch (Exception e) {
                disableService("accessing the resource subscription service", e);
            }
        }

        return !disabled;
    }

    /**
     * Log an exception and disable the servlet.
     *
     * @param message Description of activity at the time of the exception.
     * @param e The exception.
     */
    private void disableService(String message, Exception e) {
        disabled = true;
        log.error("An exception occurred while " + message + ".\nTherefore, this service will be disabled", e);
    }
}
