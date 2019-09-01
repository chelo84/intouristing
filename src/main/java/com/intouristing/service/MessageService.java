package com.intouristing.service;

import com.intouristing.exceptions.NotFoundException;
import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.model.entity.ChatGroup;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.mongo.Message;
import com.intouristing.repository.ChatGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Service
public class MessageService extends RootService {

    private final ChatGroupRepository chatGroupRepository;

    @Autowired
    public MessageService(ChatGroupRepository chatGroupRepository) {
        this.chatGroupRepository = chatGroupRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Message createMessage(SendMessageDTO sendMessageDTO, Long sentBy) {
        Message message;
        if (sendMessageDTO.getIsGroup()) {
            message = Message
                    .builder()
                    .text(sendMessageDTO.getText())
                    .chatGroup(sendMessageDTO.getChatGroup())
                    .createdAt(LocalDateTime.now())
                    .sentBy(sentBy)
                    .isGroup(true)
                    .build();

            var usersToSendMessage = chatGroupRepository.findById(sendMessageDTO.getChatGroup())
                    .orElseThrow(() -> new NotFoundException(ChatGroup.class, sendMessageDTO.getChatGroup()))
                    .getUsers();
            message.setSentTo(usersToSendMessage.stream().map(User::getId).collect(Collectors.toList()));
            return message;
        } else {
            message = Message
                    .builder()
                    .text(sendMessageDTO.getText())
                    .sentBy(sentBy)
                    .createdAt(LocalDateTime.now())
                    .sentTo(Collections.singletonList(sendMessageDTO.getSendTo()))
                    .isGroup(false)
                    .build();
        }

        return message;
    }

}
