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
package org.fujionclinical.fhir.security.common.oauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class IdTokenValidator {

    private static final Log log = LogFactory.getLog(IdTokenValidator.class);

    /**
     * see http://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation
     */
    public boolean validate(IdToken idToken, String issuerUrl, String clientId) {

        Map<String, Object> claimsMap = idToken.getClaimsMap();

        // Validate issuer id
        String tokenIss = (String) claimsMap.get("iss");

        if (tokenIss == null || !issuerUrl.contains(tokenIss)) {
            log.error("Token ISS does not match! expected [" + issuerUrl + "] received: [" + tokenIss + "]");
            return false;
        }

        // Validate sub
        if (claimsMap.get("sub") == null) {
            log.error("Token Sub is required");
            return false;
        }

        // Validate aud
        Object tokenAud = claimsMap.get("aud");

        if (tokenAud instanceof String) {
            String tokenAudStr = (String) claimsMap.get("aud");

            if (tokenAudStr == null || !tokenAudStr.equals(clientId)) {
                log.error("Token Aud does not match! expected [" + clientId + "] received: [" + tokenAud + "]");
                return false;
            }
        } else if (tokenAud instanceof List) {
            boolean found = false;
            List<String> tokenAudList = (List) claimsMap.get("aud");
            StringBuilder receivedBuf = new StringBuilder();

            for (String tokenAudStr : tokenAudList) {
                receivedBuf.append(tokenAudStr);
                receivedBuf.append(", ");

                if (tokenAudStr.equals(clientId)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                log.error(
                        "Token Aud does not match! expected [" + clientId + "] received: [" + receivedBuf.toString() + "]");
                return false;
            }
        }

        // Validate exp (seconds since epoch)
        Object tokenExpObj = claimsMap.get("exp");

        if (tokenExpObj == null) {
            log.error("Token Exp is required");
            return false;
        }

        Long tokenExp = null;

        if (tokenExpObj instanceof Integer) {
            tokenExp = ((Integer) tokenExpObj).longValue();
        } else if (tokenExpObj instanceof Long) {
            tokenExp = (Long) tokenExpObj;
        }
        Date now = new Date();

        if (now.getTime() > (tokenExp * 1000)) {
            log.error("Token Exp has expired! now [" + now.getTime() + "] exp: [" + tokenExp * 1000 + "] diff: [" + (
                    tokenExp * 1000 - now.getTime()) + "]");
            return false;
        }

        // Validate iat
        if (claimsMap.get("iat") == null) {
            log.error("Token Iat is required");
            return false;
        }

        return true;
    }
}
