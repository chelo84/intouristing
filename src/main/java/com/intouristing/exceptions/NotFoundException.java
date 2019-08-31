package com.intouristing.exceptions;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class NotFoundException extends RootException {

    public <T> NotFoundException(Class<T> clazz, Long id) {
        super("entity.of.type.not.found", clazz.getSimpleName(), id.toString());
    }

}
