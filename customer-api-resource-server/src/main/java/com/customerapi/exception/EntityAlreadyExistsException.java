package com.customerapi.exception;

public class EntityAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 8258936993459103131L;

    public EntityAlreadyExistsException(String... parameters) {
        super(parameters);
    }

}
