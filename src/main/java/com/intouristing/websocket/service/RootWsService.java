package com.intouristing.websocket.service;

import com.intouristing.exceptions.NotFoundException;
import com.intouristing.model.entity.User;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.account.AccountWsService;
import com.intouristing.websocket.controller.RootWsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Service
public class RootWsService {

    @Autowired
    SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AccountWsService accountWsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SimpUserRegistry simpUserRegistry;

    List<String> getUsers(String destination,
                          String... sendToVarargs) {
        return RootWsController.getUsers(
                destination,
                simpUserRegistry,
                Arrays.stream(sendToVarargs),
                sendToVarargs
        );
    }

    void sendToUser(String destination,
                    Object message,
                    String username) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/" + destination,
                message
        );
    }

    void send(String destination, Object message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    void sendToAnotherUser(String destination,
                           Object message,
                           String... usernames) {
        for (String username : usernames) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/" + destination,
                    message
            );
        }
    }

    void sendToAnotherUser(String destination,
                           Object message,
                           List<String> usernames) {
        this.sendToAnotherUser(
                destination,
                message,
                usernames.toArray(new String[0])
        );
    }

    public String getMessage(String message, Locale locale) {
        return messageSource.getMessage(
                new DefaultMessageSourceResolvable(message),
                locale
        );
    }

    public String getMessage(String message,
                             Locale locale,
                             Object... args) {
        return messageSource.getMessage(
                message,
                args,
                locale
        );
    }

    @Transactional(readOnly = true)
    public User getUser() {
        Long id = accountWsService.getAccount().getId();
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }
}
