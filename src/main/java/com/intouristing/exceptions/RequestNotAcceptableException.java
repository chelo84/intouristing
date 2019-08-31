package com.intouristing.exceptions;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
public class RequestNotAcceptableException extends RootException {

    public RequestNotAcceptableException() {
        super("request.accept.unauthorized");
    }

}
