package com.intouristing.exceptions;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class TokenNotFoundException extends RootException {

    public TokenNotFoundException() {
        super("token.is.mandatory");
    }

}
