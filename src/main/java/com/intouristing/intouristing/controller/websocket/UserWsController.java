package com.intouristing.intouristing.controller.websocket;

import com.intouristing.intouristing.service.account.AccountWsService;
import com.intouristing.intouristing.service.websocket.UserWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserWsController {

    private final AccountWsService accountWsService;
    private final UserWsService userWsService;
    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public UserWsController(AccountWsService accountWsService, UserWsService userWsService, SimpMessageSendingOperations messagingTemplate) {
        this.accountWsService = accountWsService;
        this.userWsService = userWsService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/users/search")
    public void search(SimpMessageHeaderAccessor headerAccessor, String message, Principal principal) {
        userWsService.search();
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/users/search", accountWsService.getAccount().getUsername());
//        return accountWsService.getAccount().getUsername();
    }

    @MessageMapping("/users/search/cancel")
    public void cancelSearch() {
        userWsService.cancelSearch();
        messagingTemplate.convertAndSendToUser(accountWsService.getAccount().getUsername(), "/queue/users/search", "Search cancelled");
        throw new RuntimeException("erro");
//        return "Search cancelled";
    }
}
