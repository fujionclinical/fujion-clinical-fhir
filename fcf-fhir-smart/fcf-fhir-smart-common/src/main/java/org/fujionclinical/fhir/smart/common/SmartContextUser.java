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
package org.fujionclinical.fhir.smart.common;

import org.fujionclinical.api.user.User;
import org.fujionclinical.api.user.UserContext;

/**
 * Implements the SMART "user" context. This also demonstrates the basic requirements for
 * implementing a SMART context. The constructor requires the SMART context name and the
 * corresponding Fujion context event name. The updateContext method implementation writes the
 * context state to the target context map. A bean definition for this class must be declared in the
 * child scope. For this class the declaration looks like this:
 * <p>
 * {@code
 * <bean id="smart.context.user" parent="smart.context.base" class="org.fujionclinical.smart.SmartContextUser" depends-on="userContext"/>
 * }
 * <p>
 * Note the bean naming convention of <code><b>smart.context.[context name]</b></code> and the
 * parent reference. The <code><b>depends-on attribute</b></code> is specific to this
 * implementation.
 */
public class SmartContextUser extends SmartContextBase {

    public SmartContextUser() {
        super("user", "CONTEXT.CHANGED.User");
    }

    @Override
    protected void updateContext(ContextMap context) {
        User user = UserContext.getActiveUser();

        if (user != null) {
            context.put("user", user.getId());
        }
    }

}
