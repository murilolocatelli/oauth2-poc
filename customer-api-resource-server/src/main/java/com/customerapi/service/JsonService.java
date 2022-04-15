package com.customerapi.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JsonService {

    String toJsonString(final Object object);

    <T> T toObject(final String jsonString, final Class<T> clazz);

    String removeNewlineAndTabFromString(final String jsonString);

    ObjectNode toObjectNode(final Object object);

}
