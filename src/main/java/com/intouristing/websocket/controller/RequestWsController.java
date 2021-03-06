package com.intouristing.websocket.controller;

import com.intouristing.model.dto.RequestDTO;
import com.intouristing.websocket.service.RequestWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.intouristing.websocket.messagemapping.MessageMappings.Request.*;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
@Slf4j
@RestController
@RequestMapping
public class RequestWsController {

    private final RequestWsService requestWsService;

    @Autowired
    public RequestWsController(RequestWsService requestWsService) {
        this.requestWsService = requestWsService;
    }

    @MessageMapping(REQUEST)
    @SendToUser(QUEUE_REQUEST)
    public void send(@Payload RequestDTO requestDTO) {
        requestWsService.send(requestDTO);
    }

    @MessageMapping(ACCEPT_REQUEST)
    public void accept(long requestId) throws Exception {
        requestWsService.accept(requestId);
    }

    @MessageMapping(DECLINE_REQUEST)
    public void decline(long requestId) throws Exception {
        requestWsService.decline(requestId);
    }
}
