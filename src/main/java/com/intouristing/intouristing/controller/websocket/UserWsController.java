package com.intouristing.intouristing.controller.websocket;

import com.intouristing.intouristing.service.account.AccountWsService;
import com.intouristing.intouristing.service.websocket.UserWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserWsController {

    private final AccountWsService accountWsService;
    private final UserWsService userWsService;

    @Autowired
    public UserWsController(AccountWsService accountWsService, UserWsService userWsService) {
        this.accountWsService = accountWsService;
        this.userWsService = userWsService;
    }

    @MessageMapping("/users/search")
    @SendTo("/users/search")
    public String search() {
        userWsService.search();
        return "User found :3";
    }

    @MessageMapping("/users/search/cancel")
    @SendTo("/users/search")
    public String cancelSearch() {
        userWsService.cancelSearch();
        return "Search cancelled";
    }
}
