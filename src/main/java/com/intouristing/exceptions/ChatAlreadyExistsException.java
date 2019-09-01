package com.intouristing.exceptions;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
public class ChatAlreadyExistsException extends RootException {

    public ChatAlreadyExistsException() {
        super("chat.already.exists");
    }

}
