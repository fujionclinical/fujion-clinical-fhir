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
package org.fujionclinical.fhir.stu3.ui.reporting.headers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.common.RegistryMap;
import org.fujion.common.RegistryMap.DuplicateAction;
import org.fujion.component.BaseComponent;
import org.fujion.component.Namespace;
import org.fujion.component.Page;
import org.fujion.page.PageUtil;
import org.fujion.websocket.ISessionLifecycle;
import org.fujion.websocket.Session;
import org.fujionclinical.fhir.stu3.ui.reporting.common.ReportConstants;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Registry for report headers to be used with print function.
 */
public class ReportHeaderRegistry implements ISessionLifecycle {

    private static final Log log = LogFactory.getLog(ReportHeaderRegistry.class);

    private static final ReportHeaderRegistry instance = new ReportHeaderRegistry();

    static {
        instance.register("user", ReportConstants.RESOURCE_PREFIX + "userReportHeader.fsp");
        instance.register("patient", ReportConstants.RESOURCE_PREFIX + "patientReportHeader.fsp");
    }

    private final Map<String, String> map = new RegistryMap<>(DuplicateAction.ERROR);

    public static ReportHeaderRegistry getInstance() {
        return instance;
    }

    /**
     * Enforce singleton instance.
     */
    private ReportHeaderRegistry() {
        super();
    }

    public void register(String headerName, String url) {
        this.map.put(headerName, url);
    }

    @Override
    public void onSessionCreate(Session session) {
        session.getPage().addEventListener("afterInitialize", (event) -> {
            ReportHeaderRegistry rhr = getInstance();
            Namespace headerRoot = new Namespace();
            headerRoot.addStyle("display", "none");
            headerRoot.setName("report_headers");

            for (Entry<String, String> entry : rhr.map.entrySet()) {
                String key = entry.getKey();
                String url = entry.getValue();
                try {
                    Namespace root = new Namespace();
                    root.setName(key);
                    PageUtil.createPage(url, root);
                    root.setParent(headerRoot);
                } catch (Exception e) {
                    log.error("Error loading report header " + key, e);
                }
            }

            session.getPage().addChild(headerRoot);
        });
    }

    @Override
    public void onSessionDestroy(Session session) {
        // NOP
    }

    /**
     * Returns the id of the root element of the specified header.
     *
     * @param headerName The header name.
     * @param page The target page.
     * @return The id of the header's root element, or null if not found.
     */
    public String getHeaderId(String headerName, Page page) {
        BaseComponent header = page.findByName("report_headers/" + headerName);
        return header == null ? null : header.getId();
    }

}
