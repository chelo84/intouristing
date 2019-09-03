package com.intouristing.websocket.service;

import com.intouristing.model.dto.mongo.MessageDTO;
import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.mongo.Message;
import com.intouristing.repository.ChatGroupRepository;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.MessageService;
import com.intouristing.service.account.AccountWsService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.MESSAGE;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Transactional
@Slf4j
@Service
public class ChatWsService extends RootWsService {

    private final AccountWsService accountWsService;
    private final MessageService messageService;
    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatWsService(AccountWsService accountWsService, MessageService messageService, ChatGroupRepository chatGroupRepository, UserRepository userRepository) {
        this.accountWsService = accountWsService;
        this.messageService = messageService;
        this.chatGroupRepository = chatGroupRepository;
        this.userRepository = userRepository;
    }

    public SendMessageDTO sendMessage(SendMessageDTO sendMessageDTO) {
        Message message = messageService.createMessage(sendMessageDTO, accountWsService.getAccount().getId());
        List<User> usersToSendMessage = userRepository.findAllById(message.getSentTo());

        for (User user : usersToSendMessage) {
            super.sendToAnotherUser(MESSAGE, MessageDTO.parseDTO(message), user.getUsername());
        }

        sendMessageDTO.setIsSent(true);
        return sendMessageDTO;
    }

    public void readMessage(ObjectId messageId) {
        messageService.readMessage(messageId, accountWsService.getAccount().getId());
    }

}
