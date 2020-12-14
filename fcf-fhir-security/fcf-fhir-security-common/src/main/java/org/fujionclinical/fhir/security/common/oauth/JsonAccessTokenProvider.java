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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.fujion.common.Assert;
import org.fujion.common.MiscUtil;
import org.fujionclinical.fhir.security.common.ICredentialProvider;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonAccessTokenProvider implements IAccessTokenProvider<JsonAccessToken> {

    private static final Log log = LogFactory.getLog(JsonAccessTokenProvider.class);

    private final ApacheHttpClientFactory apacheHttpClientFactory;

    private final IdTokenValidator idTokenValidator = new IdTokenValidator();

    public JsonAccessTokenProvider(ApacheHttpClientFactory apacheHttpClientFactory) {
        this.apacheHttpClientFactory = apacheHttpClientFactory;
    }

    protected static void setAuthorizationHeader(
            HttpRequest request,
            String clientId,
            String clientSecret) {
        String authHeader = String.format("%s:%s", clientId, clientSecret);
        String encoded = new String(org.apache.commons.codec.binary.Base64.encodeBase64(authHeader.getBytes()));
        request.addHeader("Authorization", String.format("Basic %s", encoded));
    }

    @Override
    public JsonAccessToken getAccessToken(
            String tokenEndpointUrl,
            IAccessTokenRequest<?> request) {
        String clientId = request.getClientId();
        ICredentialProvider<?> clientSecretCredentials = request.getCredentials();

        List<NameValuePair> paramPairs = new ArrayList<>();
        Map<String, String> parameters = request.getParameters();

        if (parameters != null) {
            for (String param : parameters.keySet()) {
                paramPairs.add(new BasicNameValuePair(param, parameters.get(param)));
            }
        }

        JsonObject rootResponse = post(tokenEndpointUrl, clientId, clientSecretCredentials, paramPairs);
        JsonAccessToken jsonAccessToken = buildAccessToken(rootResponse);

        String idToken = jsonAccessToken.getIdTokenStr();

        if (idToken != null) {
            // Validate the id token
            boolean idTokenValidationSuccess = idTokenValidator
                    .validate(jsonAccessToken.getIdToken(), tokenEndpointUrl, clientId);

            if (!idTokenValidationSuccess) {
                throw new RuntimeException("IdToken is not valid");
            }
        }

        return jsonAccessToken;
    }

    @Override
    public JsonAccessToken refreshAccessToken(
            String tokenEndpointUrl,
            IAccessTokenRequest<?> request,
            IAccessToken accessToken) {
        String clientId = request.getClientId();
        ICredentialProvider<?> clientSecretCredentials = request.getCredentials();

        JsonObject rootResponse = post(tokenEndpointUrl, clientId, clientSecretCredentials,
                accessToken.asNameValuePairList());
        return buildAccessToken(rootResponse);
    }

    @Override
    public IUserInfo getUserInfo(
            String userInfoEndpointUrl,
            JsonAccessToken jsonAccessToken) {
        HttpGet getRequest = new HttpGet(userInfoEndpointUrl);
        getRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        getRequest.addHeader("Authorization", String.format("Bearer %s", jsonAccessToken.getValue()));
        JsonObject jsonObject = processRequest(getRequest);
        return buildUserInfo(jsonObject);
    }

    protected JsonAccessToken buildAccessToken(JsonObject rootResponse) {
        return new JsonAccessToken(rootResponse, getResponseElement(IAccessToken.ACCESS_TOKEN, rootResponse),
                getResponseElement(IAccessToken.TOKEN_TYPE, rootResponse),
                getResponseElement(IAccessToken.EXPIRES_IN, rootResponse),
                getResponseElement(IAccessToken.SCOPE, rootResponse), getResponseElement(IAccessToken.INTENT, rootResponse),
                getResponseElement(IAccessToken.SMART_STYLE_URL, rootResponse),
                getResponseElement(IAccessToken.PATIENT, rootResponse),
                getResponseElement(IAccessToken.ENCOUNTER, rootResponse),
                getResponseElement(IAccessToken.LOCATION, rootResponse),
                Boolean.parseBoolean(getResponseElement(IAccessToken.NEED_PATIENT_BANNER, rootResponse)),
                getResponseElement(IAccessToken.RESOURCE, rootResponse),
                getResponseElement(IAccessToken.REFRESH_TOKEN, rootResponse),
                getResponseElement(IAccessToken.ID_TOKEN, rootResponse));
    }

    protected JsonUserInfo buildUserInfo(JsonObject rootResponse) {
        return new JsonUserInfo(rootResponse, getResponseElement(IUserInfo.SUB, rootResponse),
                getResponseElement(IUserInfo.NAME, rootResponse),
                getResponseElement(IUserInfo.PREFERRED_USERNAME, rootResponse));
    }

    protected JsonObject post(
            String serviceUrl,
            String clientId,
            ICredentialProvider<?> clientCredentials,
            List<NameValuePair> transferParams) {
        HttpPost postRequest = new HttpPost(serviceUrl);
        postRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");

        if (clientCredentials instanceof ClientSecretCredentials) {
            Object credentialsObj = clientCredentials.getCredentials();
            Assert.isTrue(credentialsObj instanceof String, "Client credentials are not the correct type");
            String credentialsStr = credentialsObj.toString();
            Assert.isTrue(StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(credentialsStr),
                    "Confidential client authorization requires client ID and client secret");
            setAuthorizationHeader(postRequest, clientId, credentialsStr);
        } else if (clientCredentials instanceof JWTCredentials) {
            ((JWTCredentials) clientCredentials).setAudience(serviceUrl);
        } else {
            return Assert.fail("ICredentialProvider type not supported");
        }

        try {
            postRequest.setEntity(new UrlEncodedFormEntity(transferParams));
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }

        return processRequest(postRequest);
    }

    protected JsonObject processRequest(HttpUriRequest request) {
        CloseableHttpClient httpClient = apacheHttpClientFactory.getClient();

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            log.debug(response.getStatusLine());

            if (response.getStatusLine().getStatusCode() != 200) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                throw new RuntimeException(String.format(
                        "There was a problem attempting to get the user info.\nResponse Status : %s .\nResponse Detail :%s.",
                        response.getStatusLine(), responseString));
            }

            return (JsonObject) JsonParser.parseReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    protected String getResponseElement(
            String elementKey,
            JsonObject rootResponse) {
        JsonElement jsonElement = rootResponse.get(elementKey);
        return jsonElement == null ? null : jsonElement.getAsString();
    }

}
