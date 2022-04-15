package com.authorizationserver.exception;

public class UnauthorizedException extends BaseException {

    private static final long serialVersionUID = -6458448971420375958L;

    public UnauthorizedException(String... parameters) {
        super(parameters);
    }

}
