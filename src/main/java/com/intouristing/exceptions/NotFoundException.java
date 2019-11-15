package com.intouristing.exceptions;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class NotFoundException extends RootException {

    public <T> NotFoundException(Class<T> clazz, Long id) {
        super(
                "entity.of.type.not.found",
                clazz.getSimpleName(),
                id.toString()
        );
    }

    public <T> NotFoundException(Class<T> clazz, Long... ids) {
        super(
                "entity.of.type.not.found",
                clazz.getSimpleName(),
                Stream.of(ids)
                        .map(Object::toString)
                        .collect(Collectors.joining(" & "))
        );
    }

}
