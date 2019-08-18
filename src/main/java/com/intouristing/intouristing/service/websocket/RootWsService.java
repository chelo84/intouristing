package com.intouristing.intouristing.service.websocket;

import com.intouristing.intouristing.exceptions.NotFoundException;
import com.intouristing.intouristing.model.entity.User;
import com.intouristing.intouristing.model.repository.UserRepository;
import com.intouristing.intouristing.service.account.AccountWsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Service
public class RootWsService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AccountWsService accountWsService;
    @Autowired
    private UserRepository userRepository;

    public String getMessage(String message, Locale locale) {
        return messageSource.getMessage(new DefaultMessageSourceResolvable(message), locale);
    }

    public String getMessage(String message, Locale locale, Object... args) {
        return messageSource.getMessage(message, args, locale);
    }

    public User getUser() {
        Long id = accountWsService.getAccount().getId();
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }
}
