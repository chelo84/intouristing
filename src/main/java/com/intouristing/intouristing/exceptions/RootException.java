package com.intouristing.intouristing.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marcelo Lacroix on 11/08/2019.
 */
@Setter
@Getter
public class RootException extends RuntimeException {

    private String customMessage;
    private String[] params;

    public RootException(String customMessage) {
        this.customMessage = customMessage;
    }

    public RootException(String customMessage, String... params) {
        this.customMessage = customMessage;
        this.params = params;
    }

    @Override
    public String getMessage() {
        return this.customMessage;
    }
}
