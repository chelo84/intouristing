package com.intouristing.exceptions;

public class RequestAlreadySentException extends RootException {

    public RequestAlreadySentException() {
        this("request.already.sent");
    }

    public RequestAlreadySentException(String customMessage) {
        super(customMessage);
    }

    public RequestAlreadySentException(String customMessage, String... params) {
        super(customMessage, params);
    }

}
