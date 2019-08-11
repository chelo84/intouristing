package com.intouristing.intouristing.exceptions;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class TokenExpiredException extends RootException {

    public TokenExpiredException() {
        super("token.expired");
    }

}