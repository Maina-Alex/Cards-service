package com.amaina.cards.exception;

public class UnAuthorizedOperationException extends RuntimeException{
    public UnAuthorizedOperationException(String message) {
        super(message);
    }
}
