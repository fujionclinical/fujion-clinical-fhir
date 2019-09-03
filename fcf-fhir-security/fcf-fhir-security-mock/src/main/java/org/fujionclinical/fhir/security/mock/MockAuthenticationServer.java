package org.fujionclinical.fhir.security.mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Controller for a mock OAuth2 authentication service.
 *
 */
@Controller
@RequestMapping("/auth")
public class MockAuthenticationServer {

    private static final int INITIAL_EXPIRATION_INTERVAL = 10;

    private static class LaunchContext {

        long expirationTime;

        final int expiresIn;

        final Map<String, String> map;

        LaunchContext(Map<String, String> map, int expiresIn, boolean allowRefresh) {
            this.map = map;
            this.expiresIn = expiresIn;
            setExpirationTime(INITIAL_EXPIRATION_INTERVAL);
            map.put("access_token", UUID.randomUUID().toString());
            map.put("token_type", "Bearer");
            map.put("expires_in", expiresIn + "");

            if (allowRefresh) {
                map.put("refresh_token", UUID.randomUUID().toString());
            }
        }

        private void setExpirationTime(int expirationInterval) {
            expirationTime = System.currentTimeMillis() + expirationInterval * 1000;
        }

        void resetExpirationTime() {
            setExpirationTime(expiresIn);
        }
    }

    private final Map<String, LaunchContext> contexts = Collections.synchronizedMap(new HashMap<>());

    @Value("${oauth.mock.allow_refresh:false}")
    private boolean allowRefresh;

    @Value("${oauth.mock.expires_in:3600}")
    private int expiresIn;

    @RequestMapping(
            path = "/Launch",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity launch(@RequestBody Map<String, Object> payload) {
        Object params = payload.get("parameters");

        if (!(params instanceof Map)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        String launchId = UUID.randomUUID().toString();
        contexts.put(launchId, new LaunchContext((Map) params, expiresIn, allowRefresh));
        Map<String, Object> body = new HashMap<>();
        body.put("parameters", params);
        body.put("launch_id", launchId);
        body.put("created_by", "Mock Auth Service");
        body.put("created_at", new Date().toString());
        return new ResponseEntity(body, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/authorize",
            method = RequestMethod.GET)
    public Object authorize(HttpServletRequest request) {
        removeExpiredContexts();
        String response_type = request.getParameter("response_type");
        String client_id = request.getParameter("client_id");
        String scope = request.getParameter("scope");
        String redirect_uri = request.getParameter("redirect_uri");
        String aud = request.getParameter("aud");
        String launch = request.getParameter("launch");
        String state = request.getParameter("state");
        LaunchContext context = launch == null ? null : contexts.get(launch);

        if (!"code".equals(response_type) || client_id == null || scope == null || redirect_uri == null
                || aud == null || context == null || state == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        context.map.put("scope", scope);
        redirect_uri += "?code=" + launch + "&state=" + state;
        return "redirect:" + redirect_uri;
    }

    @RequestMapping(
            path = "/token",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity token(@RequestParam Map<String, String> payload) {
        boolean badRequest = false;
        removeExpiredContexts();
        String code = payload.get("code");
        String grant_type = payload.get("grant_type");
        LaunchContext context = code == null ? null : contexts.remove(code);

        if (context == null) {
            badRequest = true;
        } else if (allowRefresh && "refresh_token".equals(grant_type)) {
            context.resetExpirationTime();
            contexts.put(code, context);
        } else if ("authorization_code".equals(grant_type)) {
            context.resetExpirationTime();
        } else {
            badRequest = true;
        }

        return badRequest ? new ResponseEntity(HttpStatus.BAD_REQUEST) : new ResponseEntity(context.map, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/ping",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String ping() {
        removeExpiredContexts();
        return "<h1>Active contexts: " + contexts.size() + "</h1>";
    }

    private void removeExpiredContexts() {
        synchronized (contexts) {
            long current = System.currentTimeMillis();
            Iterator<String> iter = contexts.keySet().iterator();

            while (iter.hasNext()) {
                String key = iter.next();

                if (current > contexts.get(key).expirationTime) {
                    iter.remove();
                }
            }
        }
    }
}
