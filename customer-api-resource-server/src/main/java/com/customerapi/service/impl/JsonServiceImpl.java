package com.customerapi.service.impl;

import com.customerapi.service.JsonService;
import com.customerapi.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonServiceImpl implements JsonService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String toJsonString(final Object object) {

        return Optional.ofNullable(object)
            .map(t -> {
                try {
                    return objectMapper.writeValueAsString(object);
                } catch (JsonProcessingException e) {
                    return null;
                }
            })
            .orElse(null);
    }

    @Override
    public <T> T toObject(final String jsonString, final Class<T> clazz) {

        return Optional.ofNullable(jsonString)
            .filter(t -> !StringUtils.isEmptyTrim(t))
            .map(t -> {
                try {
                    return objectMapper.readValue(jsonString, clazz);
                } catch (IOException e) {
                    return null;
                }
            })
            .orElse(null);
    }

    @Override
    public String removeNewlineAndTabFromString(final String jsonString) {

        return Optional.ofNullable(jsonString)
            .filter(t -> !StringUtils.isEmptyTrim(t))
            .map(t -> jsonString.replaceAll("\"", "").replaceAll("\n", ""))
            .orElse(null);
    }

    @Override
    public ObjectNode toObjectNode(final Object object) {

        return (ObjectNode) Optional.ofNullable(object)
            .map(t -> {
                try {
                    return objectMapper.readTree(toJsonString(object));
                } catch (IOException e) {
                    return null;
                }
            })
            .orElse(null);
    }

}
