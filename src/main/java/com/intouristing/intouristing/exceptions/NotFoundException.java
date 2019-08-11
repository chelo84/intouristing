package com.intouristing.intouristing.exceptions;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class NotFoundException extends Exception {
    public <T> NotFoundException(Class<T> clazz, Long id) {
        super(String.format("Entity of type %s for id %s was not found", clazz.getName(), id));
    }
}
