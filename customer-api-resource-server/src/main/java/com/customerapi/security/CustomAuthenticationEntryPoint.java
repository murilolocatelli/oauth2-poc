package com.customerapi.security;

import static java.text.MessageFormat.format;

import com.customerapi.service.JsonService;
import com.customerapi.dto.ResponseError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private JsonService jsonService;

    @Override
    public void commence(
        final HttpServletRequest request, final HttpServletResponse response,
        final AuthenticationException authException) throws IOException {

        final List<ResponseError> responseError = Collections.singletonList(
            ResponseError.builder()
                .developerMessage(format("Unauthenticated - {0}", authException.getMessage()))
                .userMessage("You must be authenticated to perform this operation")
                .build());

        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().println(jsonService.toJsonString(responseError));
    }

}
