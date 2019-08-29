package com.intouristing.websocket.service;

import com.intouristing.exceptions.NotFoundException;
import com.intouristing.model.entity.User;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.account.AccountWsService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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

    List<String> getUsers(String destination, String... sendToVarargs) {
        if (isNull(sendToVarargs)) {
            throw new RuntimeException("user.destination.required");
        }

        String subDestination = "/user/queue" + destination;
        Set<SimpSubscription> subscriptionsSet = simpUserRegistry.findSubscriptions((simpSubscription) -> simpSubscription.getDestination().equals(subDestination));
        SimpSubscription[] subscriptions = subscriptionsSet.toArray(new SimpSubscription[0]);
        List<String> userList = new ArrayList<>();
        List<String> sendTo = Arrays.stream(sendToVarargs).map(String::toLowerCase).collect(Collectors.toList());
        if (ArrayUtils.isNotEmpty(subscriptions)) {
            userList = Stream.of(subscriptionsSet.toArray(subscriptions))
                    .map(SimpSubscription::getSession)
                    .map(SimpSession::getUser)
                    .map(SimpUser::getName)
                    .map(String::toLowerCase)
                    .filter(sendTo::contains)
                    .collect(Collectors.toList());
        }

        return userList;
    }

    void sendToUser(String destination, Object message, String username) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/" + destination,
                message
        );
    }

    void send(String destination, Object message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    void sendToAnotherUser(String destination, Object message, String sentBy, String... usernames) {
        for (String username : usernames) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/" + destination,
                    message,
                    nonNull(sentBy) ? Map.of("SentBy", sentBy) : null
            );
        }
    }

    void sendToAnotherUser(String destination, Object message, String sentBy, List<String> usernames) {
        this.sendToAnotherUser(
                destination,
                message,
                sentBy,
                usernames.toArray(new String[0])
        );
    }

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
