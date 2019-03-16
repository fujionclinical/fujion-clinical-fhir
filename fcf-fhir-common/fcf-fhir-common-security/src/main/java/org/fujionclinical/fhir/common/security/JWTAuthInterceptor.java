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

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.fujion.common.MiscUtil;
import org.fujionclinical.api.spring.PropertyBasedConfigurator;
import org.fujionclinical.api.spring.PropertyBasedConfigurator.Param;
import org.fujionclinical.fhir.common.security.oauth.*;

import java.net.URL;
import java.util.UUID;

/*
import org.hspconsortium.client.auth.access.IAccessToken;
import org.hspconsortium.client.auth.access.IAccessTokenProvider;
import org.hspconsortium.client.auth.access.JsonAccessTokenProvider;
import org.hspconsortium.client.auth.credentials.JWTCredentials;
import org.hspconsortium.client.session.ApacheHttpClientFactory;
import org.hspconsortium.client.session.clientcredentials.ClientCredentialsAccessTokenRequest;
*/

/**
 * Authentication interceptor supporting JWT authentication.
 */
public class JWTAuthInterceptor extends AbstractAuthInterceptor {

    @Param(property = "authentication.key.issuer")
    private final String issuer = "fujion";

    @Param(property = "authentication.subject")
    private final String subject = "fujion";

    @Param(property = "authentication.token.duration")
    private final long duration = 300;

    @Param(property = "authentication.scopes")
    private final String scopes = "user/*.*,patient/*.read";

    @Param(property = "authentication.proxy.port")
    private final int proxyPort = -1;

    private final Scopes requestedScopes;

    private final IAccessTokenProvider<?> tokenProvider;

    @Param(property = "authentication.key.location")
    private String webKey;

    @Param(property = "authentication.token.provider")
    private String tokenProviderUrl;

    @Param(property = "authentication.audience")
    private String audience;

    @Param(property = "authentication.proxy.host")
    private String proxyHost;

    @Param(property = "authentication.proxy.user")
    private String proxyUser;

    @Param(property = "authentication.proxy.password")
    private String proxyPassword;

    @Param(property = "authentication.proxy.timeout.connection")
    private int httpConnectionTimeOut;

    @Param(property = "authentication.proxy.timeout.request")
    private int httpRequestTimeOut;

    private IAccessToken accessToken;

    private JWTCredentials jwtCredentials;

    public JWTAuthInterceptor(PropertyBasedConfigurator config) throws Exception {
        super(config, "Bearer");
        ApacheHttpClientFactory factory = new ApacheHttpClientFactory(proxyHost, proxyPort, proxyUser, proxyPassword,
                httpConnectionTimeOut, httpRequestTimeOut);
        tokenProvider = new JsonAccessTokenProvider(factory);
        requestedScopes = new Scopes();

        for (String scope : scopes.split("\\,")) {
            scope = scope.trim();

            if (!scope.isEmpty()) {
                requestedScopes.add(new SimpleScope(scope));
            }
        }

    }

    @Override
    public synchronized String getCredentials() {
        if (accessToken == null || accessToken.isExpired()) {
            accessToken = getAccessToken();
        }

        return accessToken.getValue();
    }

    private IAccessToken getAccessToken() {
        if (jwtCredentials == null) {
            initCredentials();
        }

        jwtCredentials.setTokenReference(UUID.randomUUID().toString());
        ClientCredentialsAccessTokenRequest<JWTCredentials> tokenRequest = new ClientCredentialsAccessTokenRequest<>(issuer,
                jwtCredentials, requestedScopes);

        return tokenProvider.getAccessToken(tokenProviderUrl, tokenRequest);
    }

    private void initCredentials() {
        try {
            // RSA signatures require a public and private RSA key pair, the public key
            // must be made known to the JWS recipient in order to verify the signatures
            URL url = getClass().getResource(webKey);
            JWKSet jwks = JWKSet.load(url);
            RSAKey rsaKey = (RSAKey) jwks.getKeys().get(0);

            jwtCredentials = new JWTCredentials(rsaKey.toRSAPrivateKey());
            jwtCredentials.setIssuer(issuer);
            jwtCredentials.setSubject(subject);
            jwtCredentials.setAudience(audience.isEmpty() ? tokenProviderUrl : audience);
            jwtCredentials.setDuration(duration);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

}
