package com.authorizationserver.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Utils {

    public static String passwordEncoded(String password) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.encode(password);
    }

}
