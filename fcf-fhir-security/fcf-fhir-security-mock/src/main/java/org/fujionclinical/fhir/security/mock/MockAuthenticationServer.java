package org.fujionclinical.fhir.security.mock;

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


    private final Map<String, Map<String, String>> contexts = Collections.synchronizedMap(new HashMap<>());

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
        contexts.put(launchId, (Map) params);
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
        String response_type = request.getParameter("response_type");
        String client_id = request.getParameter("client_id");
        String scope = request.getParameter("scope");
        String redirect_uri = request.getParameter("redirect_uri");
        String aud = request.getParameter("aud");
        String launch = request.getParameter("launch");
        String state = request.getParameter("state");
        Map<String, String> context = launch == null ? null : contexts.get(launch);

        if (!"code".equals(response_type) || client_id == null || scope == null || redirect_uri == null
                || aud == null || context == null || state == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        context.put("scope", scope);
        redirect_uri += "?code=" + launch + "&state=" + state;
        return "redirect:" + redirect_uri;
    }

    @RequestMapping(
            path = "/token",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity token(@RequestParam Map<String, String> payload) {
        String code = payload.get("code");
        String grant_type = payload.get("grant_type");
        Map<String, String> body = code == null ? null : contexts.remove(code);

        if (body == null || !"authorization_code".equals(grant_type)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        body.put("access_token", UUID.randomUUID().toString());
        body.put("token_type", "Bearer");
        return new ResponseEntity(body, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/ping",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String ping() {
        return "<h1>Received Ping Request</h1>";
    }
}
