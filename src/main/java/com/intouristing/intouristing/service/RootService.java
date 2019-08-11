package com.intouristing.intouristing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Created by Marcelo Lacroix on 11/08/2019.
 */
@Service
public class RootService {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String message, Locale locale) {
        return messageSource.getMessage(new DefaultMessageSourceResolvable(message), locale);
    }

    public String getMessage(String message, Locale locale, Object... args) {
        return messageSource.getMessage(message, args, locale);
    }
}
