package com.intouristing.websocket.controller;

import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.websocket.service.ChatWsService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.MESSAGE;
import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.READ_MESSAGE;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Slf4j
@RestController
@RequestMapping
public class ChatWsController extends RootWsController {

    private final ChatWsService chatWsService;

    public ChatWsController(ChatWsService chatWsService) {
        this.chatWsService = chatWsService;
    }

    @MessageMapping(MESSAGE)
    @SendToUser(MESSAGE)
    public SendMessageDTO sendMessage(@Payload SendMessageDTO sendMessageDTO) {
        sendMessageDTO = chatWsService.sendMessage(sendMessageDTO);

        return sendMessageDTO;
    }

    @MessageMapping(READ_MESSAGE)
    public void readMessage(String messageId) {
        chatWsService.readMessage(new ObjectId(messageId));
    }

}
