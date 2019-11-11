package com.intouristing.service;

import com.intouristing.model.entity.User;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 11/08/2019.
 */
@Service
public class RootService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;

    public String getMessage(String message, Locale locale) {
        return messageSource.getMessage(new DefaultMessageSourceResolvable(message), locale);
    }

    public String getMessage(String message, Locale locale, Object... args) {
        return messageSource.getMessage(message, args, locale);
    }

    public User getUser() {
        User user = userRepository.findById(accountService.getAccount().getId()).orElse(null);
        assert nonNull(user);
        return user;
    }
}
