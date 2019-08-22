package com.intouristing.intouristing.controller.websocket;

import com.intouristing.intouristing.model.dto.SearchDTO;
import com.intouristing.intouristing.model.dto.UserDTO;
import com.intouristing.intouristing.service.account.AccountWsService;
import com.intouristing.intouristing.service.websocket.UserWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.intouristing.intouristing.controller.websocket.WebSocketMessageMapping.*;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@RestController
@RequestMapping
public class UserWsController extends RootWsController {

    private final AccountWsService accountWsService;
    private final UserWsService userWsService;

    @Autowired
    public UserWsController(AccountWsService accountWsService, UserWsService userWsService) {
        this.accountWsService = accountWsService;
        this.userWsService = userWsService;
    }

    @MessageMapping(SEARCH)
    @SendToUser(QUEUE_SEARCH)
    public SearchDTO search(double radius) {
        List<UserDTO> users = userWsService.search(radius)
                .stream()
                .map(UserDTO::parseDTO)
                .collect(Collectors.toList());

        return SearchDTO
                .builder()
                .users(users)
                .build();
    }

    @MessageMapping(SEND_MESSAGE)
    public void message(@Header("sendTo") String sendTo, @Header("group") Boolean group, String message, Principal principal) {

        if (isTrue(group)) {
            // NOT YET IMPLEMENTED
        }

        List<String> users = super.getUsers(SEARCH, sendTo);
        super.sendToAnotherUser("/message", message, principal.getName(), users);
    }
}
