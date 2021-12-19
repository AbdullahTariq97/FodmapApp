package com.sky.fodmap.service.header_authentication;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class AuthEntryPoint extends BasicAuthenticationEntryPoint {

    private final Map<String, String> OVP_00306;

    public AuthEntryPoint() {
        OVP_00306 = Map.of("Description", "security failure");
        setRealmName("fodmap_realm");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        if (authException != null) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new JSONObject(OVP_00306).toString());
        }
    }
}
