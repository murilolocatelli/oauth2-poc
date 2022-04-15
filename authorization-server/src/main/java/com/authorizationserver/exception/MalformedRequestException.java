package com.authorizationserver.exception;

public class MalformedRequestException extends BaseException {

    private static final long serialVersionUID = 8780846167874151319L;

    public MalformedRequestException(String... parameters) {
        super(parameters);
    }

}
