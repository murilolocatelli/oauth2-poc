package com.customerapi.controller;

import static java.text.MessageFormat.format;

import com.customerapi.dto.ResponseError;
import com.customerapi.exception.EntityAlreadyExistsException;
import com.customerapi.exception.EntityNotFoundException;
import com.customerapi.exception.MalformedRequestException;
import com.customerapi.exception.MissingParameterException;
import com.customerapi.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class CustomControllerAdvice {

    private static final String ZONED_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String LOCAL_DATE_PATTERN = "yyyy-MM-dd";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(final MethodArgumentNotValidException ex) {

        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        final List<ResponseError> errorMessages = new ArrayList<>();

        fieldErrors.forEach(fieldError ->
            errorMessages.add(this.getResponseError(fieldError)));

        return errorMessages;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(final HttpMessageNotReadableException ex) {

        final List<ResponseError> errorMessages = new ArrayList<>();

        final Throwable cause = ex.getCause();

        if (cause instanceof JsonMappingException) {

            final JsonMappingException jsonMappingException = (JsonMappingException) cause;

            final String field = this.getField(jsonMappingException);

            final String message = ex.getMessage();

            errorMessages.add(this.getResponseError(message, field));

        } else {

            errorMessages.add(this.getMalformedRequestResponseError());
        }

        return errorMessages;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(final BindException ex) {

        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        final List<ResponseError> errorMessages = new ArrayList<>();

        fieldErrors.forEach(fieldError -> {

            if ("typeMismatch".equalsIgnoreCase(fieldError.getCode())) {

                final String message = fieldError.getDefaultMessage();

                errorMessages.add(this.getResponseError(message, fieldError.getField()));

            } else {

                errorMessages.add(this.getResponseError(fieldError));
            }
        });

        return errorMessages;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(final MethodArgumentTypeMismatchException ex) {
        return Collections.singletonList(this.getMalformedRequestResponseError());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public List<ResponseError> exception(final HttpRequestMethodNotSupportedException ex) {

        return Collections.singletonList(ResponseError.builder()
            .developerMessage("Method not allowed")
            .userMessage("Method not allowed")
            .build());
    }

    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ResponseError> exception(final EntityNotFoundException ex) {
        return Collections.singletonList(ResponseError.builder()
            .developerMessage(format("{0} not found", (Object[]) ex.getParameters()))
            .userMessage(format("You attempted to get a {0}, but did not find any", (Object[]) ex.getParameters()))
            .build());
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<ResponseError> exception(final UnauthorizedException ex) {
        return Collections.singletonList(ResponseError.builder()
            .developerMessage(format("Unauthorized - {0}", (Object[]) ex.getParameters()))
            .userMessage("You are not authorized to perform this operation")
            .build());
    }

    @ResponseBody
    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ResponseError> exception(final EntityAlreadyExistsException ex) {
        return Collections.singletonList(ResponseError.builder()
            .developerMessage(format("{0} already exists", (Object[]) ex.getParameters()))
            .userMessage(format("You attempted to create {0}, but already exists", (Object[]) ex.getParameters()))
            .build());
    }

    @ResponseBody
    @ExceptionHandler(MissingParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ResponseError> exception(final MissingParameterException ex) {
        return Collections.singletonList(this.getMissingParameterResponseError(ex.getParameters()));
    }

    @ResponseBody
    @ExceptionHandler(MalformedRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ResponseError> exception(final MalformedRequestException ex) {
        return Collections.singletonList(this.getMalformedRequestResponseError());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<ResponseError> exception(final Exception ex) {
        final String developerMessage = "Internal server error {0}";
        final String userMessage = "Was encountered an error when processing your request. We apologize for the inconvenience";

        return Collections.singletonList(ResponseError.builder()
            .developerMessage(format(developerMessage, ex.toString()))
            .userMessage(format(userMessage, ex.toString()))
            .build());
    }

    private ResponseError getResponseError(String message, final String fieldName) {

        message = Optional.ofNullable(message).orElse("");

        if (message.contains("java.lang.Short")
            || message.contains("java.lang.Integer")
            || message.contains("java.lang.Long")) {

            final String developerMessage =
                format("Invalid parameter {0} - it must be filled with a valid integer number", fieldName);

            final String userMessage =
                format("Invalid field {0} - it must be filled with a valid integer number", fieldName);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.lang.Double")
            || message.contains("java.lang.Float")) {

            final String developerMessage =
                format("Invalid parameter {0} - it must be filled with a valid number", fieldName);

            final String userMessage =
                format("Invalid field {0} - it must be filled with a valid number", fieldName);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.lang.Boolean")) {

            final String developerMessage =
                format("Invalid parameter {0} - it must be filled with a valid boolean (true or false)", fieldName);

            final String userMessage =
                format("Invalid field {0} - it must be filled with a true or false", fieldName);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.time.ZonedDateTime")) {

            final String developerMessage =
                format(
                    "Invalid parameter {0} - it must be filled with a valid date in pattern {1}",
                    fieldName, ZONED_DATE_TIME_PATTERN);

            final String userMessage =
                format(
                    "Invalid field {0} - it must be filled with a valid date in pattern {1}",
                    fieldName, ZONED_DATE_TIME_PATTERN);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.time.LocalDateTime")) {

            final String developerMessage =
                format(
                    "Invalid parameter {0} - it must be filled with a valid date in pattern {1}",
                    fieldName, LOCAL_DATE_TIME_PATTERN);

            final String userMessage =
                format(
                    "Invalid field {0} - it must be filled with a valid date in pattern {1}",
                    fieldName, LOCAL_DATE_TIME_PATTERN);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.time.LocalDate")) {

            final String developerMessage =
                format(
                    "Invalid parameter {0} - it must be filled with a valid date in pattern {1}",
                    fieldName, LOCAL_DATE_PATTERN);

            final String userMessage =
                format(
                    "Invalid field {0} - it must be filled with a valid date in pattern {1}",
                    fieldName, LOCAL_DATE_PATTERN);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();
        } else {

            return this.getMalformedRequestResponseError();
        }
    }

    private ResponseError getResponseError(final FieldError fieldError) {

        final String fieldName = fieldError.getField();

        if ("NotNull".equalsIgnoreCase(fieldError.getCode())
            || "NotBlank".equalsIgnoreCase(fieldError.getCode())) {

            return this.getMissingParameterResponseError(new String[]{fieldName});

        } else if ("Min".equalsIgnoreCase(fieldError.getCode())) {

            final String developerMessage = format(
                "Invalid parameter {0} - it must be filled with a value greater than or equal to {1}",
                fieldName,
                this.getValue(fieldError));

            final String userMessage = format(
                "Invalid field {0} - it must be filled with a value greater than or equal to {1}",
                fieldName,
                this.getValue(fieldError));

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if ("Max".equalsIgnoreCase(fieldError.getCode())) {

            final String developerMessage = format(
                "Invalid parameter {0} - it must be filled with a value less than or equal to {1}",
                fieldName,
                this.getValue(fieldError));
            final String userMessage = format(
                "Invalid field {0} - it must be filled with a value less than or equal to {1}",
                fieldName,
                this.getValue(fieldError));

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else {

            return this.getMalformedRequestResponseError();
        }
    }

    private ResponseError getMalformedRequestResponseError() {
        return ResponseError.builder()
            .developerMessage("Malformed request")
            .userMessage("Malformed request")
            .build();
    }

    private ResponseError getMissingParameterResponseError(final String[] parameters) {
        return ResponseError.builder()
            .developerMessage(format("Missing parameter {0}", parameters))
            .userMessage(format("Field {0} is required and can not be empty", parameters))
            .build();
    }

    private Long getValue(final FieldError fieldError) {
        return (Long) Optional.ofNullable(fieldError.getArguments())
            .filter(t -> t.length > 1)
            .map(t -> t[1])
            .orElse(null);
    }

    private String getField(final JsonMappingException jsonMappingException) {
        return jsonMappingException.getPath().stream()
            .map(t -> t.getFieldName() != null ? t.getFieldName() : "[" + t.getIndex() + "]")
            .reduce((t, u) -> {
                if (u.contains("[")) {
                    return t + u;
                } else {
                    return t + "." + u;
                }
            })
            .orElse(null);
    }

}
