package com.customerapi.exception;

public class EntityNotFoundException extends BaseException {

    private static final long serialVersionUID = 6055443942387286895L;

    public EntityNotFoundException(String... parameters) {
        super(parameters);
    }

}
