package com.customerapi.util;

public class StringUtils {

    public static boolean isEmptyTrim(final String value) {
        return org.springframework.util.StringUtils.isEmpty(value) || value.trim().isEmpty();
    }

}
