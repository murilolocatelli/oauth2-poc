package com.customerapi.security;

import static java.text.MessageFormat.format;

import com.customerapi.service.JsonService;
import com.customerapi.dto.ResponseError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private JsonService jsonService;

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
        final AccessDeniedException authException) throws IOException {

        final List<ResponseError> responseError = Collections.singletonList(
            ResponseError.builder()
                .developerMessage(format("Unauthorized - {0}", authException.getMessage()))
                .userMessage("You are not authorized to perform this operation")
                .build());

        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().println(jsonService.toJsonString(responseError));
    }

}
