package com.intouristing.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marcelo Lacroix on 11/08/2019.
 */
@Setter
@Getter
public abstract class RootException extends RuntimeException {

    private String customMessage;
    private String[] params;

    RootException(String customMessage) {
        this.customMessage = customMessage;
    }

    RootException(String customMessage, String... params) {
        this.customMessage = customMessage;
        this.params = params;
    }

    @Override
    public String getMessage() {
        return this.customMessage;
    }
}
