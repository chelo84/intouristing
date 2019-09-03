package com.intouristing.websocket.controller;

import com.intouristing.model.dto.SearchDTO;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.websocket.service.SearchWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.intouristing.websocket.messagemapping.MessageMappings.Search.QUEUE_SEARCH;
import static com.intouristing.websocket.messagemapping.MessageMappings.Search.SEARCH;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@RestController
@RequestMapping
public class SearchWsController extends RootWsController {

    private final SearchWsService searchWsService;

    @Autowired
    public SearchWsController(SearchWsService searchWsService) {
        this.searchWsService = searchWsService;
    }

    @MessageMapping(SEARCH)
    @SendToUser(QUEUE_SEARCH)
    public SearchDTO search(double radius) {
        List<UserDTO> users = searchWsService.search(radius)
                .stream()
                .map(UserDTO::parseDTO)
                .collect(Collectors.toList());

        return SearchDTO
                .builder()
                .users(users)
                .build();
    }

//    @MessageMapping(SEND_MESSAGE)
//    public void message(@Header("sendTo") String sendTo, @Header("group") Boolean group, String message, Principal principal) {
//
//        if (isTrue(group)) {
//            // NOT YET IMPLEMENTED
//        }
//
//        List<String> users = super.getUsers(SEARCH, sendTo);
//        super.sendToAnotherUser("/message", message, principal.getName(), users);
//    }
}
