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
package org.fujionclinical.fhir.demo.setup;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Perform initial setup of demo web app. This subclasses BeanPostProcessor to ensure that it is
 * executed early in application startup.
 */
public class DemoSetup implements BeanPostProcessor {

    private static final Log log = LogFactory.getLog(DemoSetup.class);

    public DemoSetup(
            BasicDataSource ds,
            Resource sqlResource) {
        log.info("Performing setup of demo application...");

        try (Connection conn = ds.getConnection(); InputStream is = sqlResource.getInputStream()) {
            String sql = IOUtils.toString(is, StandardCharsets.UTF_8);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            log.info("Completed setup of demo application.");
        } catch (Exception e) {
            log.error("Error during demo setup.  This can occur if setup has already been processed.\n\n" + e.getMessage());
        }
    }

}
