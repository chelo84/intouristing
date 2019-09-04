package com.intouristing.service;

import com.intouristing.exceptions.NotFoundException;
import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.model.entity.ChatGroup;
import com.intouristing.model.entity.PrivateChat;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.mongo.Message;
import com.intouristing.model.entity.mongo.ReadBy;
import com.intouristing.repository.ChatGroupRepository;
import com.intouristing.repository.mongo.MessageRepository;
import com.intouristing.service.account.AccountWsService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Service
public class MessageService extends RootService {

    private final ChatGroupRepository chatGroupRepository;
    private final ChatService chatService;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(ChatGroupRepository chatGroupRepository, ChatService chatService, MessageRepository messageRepository, AccountWsService accountWsService) {
        this.chatGroupRepository = chatGroupRepository;
        this.chatService = chatService;
        this.messageRepository = messageRepository;
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
                    .getUsers()
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            if (!usersToSendMessage.contains(sentBy)) {
                throw new RuntimeException("not.supposed.send.message.to.group");
            }

            message.setSentTo(usersToSendMessage.stream().filter(id -> !id.equals(sentBy)).collect(Collectors.toList()));
        } else {
            message = Message
                    .builder()
                    .text(sendMessageDTO.getText())
                    .sentBy(sentBy)
                    .createdAt(LocalDateTime.now())
                    .sentTo(Collections.singletonList(sendMessageDTO.getSendTo()))
                    .isGroup(false)
                    .privateChat(Optional.ofNullable(chatService.findPrivateChat(sentBy, sendMessageDTO.getSendTo()))
                            .orElseThrow(() -> new NotFoundException(PrivateChat.class, sentBy, sendMessageDTO.getSendTo())))
                    .build();
        }
        message.setId(new ObjectId());

        return messageRepository.save(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Message readMessage(ObjectId messageId, Long userId) {
        Message message = messageRepository.findById(messageId).get();

        if (message.getSentTo().contains(userId)) {
            List<ReadBy> readBy = Optional.ofNullable(message.getReadBy()).orElseGet(ArrayList::new);
            readBy.add(this.createReadBy(userId));
            message.setReadBy(readBy);
            message.setReadByAll(readBy.stream().map(ReadBy::getUser).collect(Collectors.toList()).containsAll(message.getSentTo()));
            return messageRepository.save(message);
        }

        throw new RuntimeException("not.supposed.read.message");
    }

    private ReadBy createReadBy(Long userId) {
        return ReadBy
                .builder()
                .user(userId)
                .readAt(LocalDateTime.now())
                .build();
    }

}
