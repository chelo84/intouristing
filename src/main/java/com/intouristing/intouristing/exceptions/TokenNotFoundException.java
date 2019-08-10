package com.intouristing.intouristing.exceptions;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class TokenNotFoundException extends Exception {

    public TokenNotFoundException() {
        super("Token is mandatory");
    }

}
