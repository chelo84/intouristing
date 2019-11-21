package com.intouristing.websocket.service;

import com.intouristing.model.dto.mongo.MessageDTO;
import com.intouristing.model.dto.mongo.ReadMessageDTO;
import com.intouristing.model.dto.mongo.ReadMessageUserDTO;
import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.mongo.Message;
import com.intouristing.model.entity.mongo.MessageUser;
import com.intouristing.repository.ChatGroupRepository;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.MessageService;
import com.intouristing.service.account.AccountWsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.MESSAGE;
import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.READ_MESSAGE;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Slf4j
@Service
public class ChatWsService extends RootWsService {

    private final AccountWsService accountWsService;
    private final MessageService messageService;
    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatWsService(AccountWsService accountWsService,
                         MessageService messageService,
                         ChatGroupRepository chatGroupRepository,
                         UserRepository userRepository) {
        this.accountWsService = accountWsService;
        this.messageService = messageService;
        this.chatGroupRepository = chatGroupRepository;
        this.userRepository = userRepository;
    }

    private List<User> getMessageUsers(Message message) {
        return userRepository.findAllById(message.getSentTo()
                .stream()
                .map(MessageUser::getUserId)
                .collect(Collectors.toList())
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SendMessageDTO sendMessage(SendMessageDTO sendMessageDTO) {
        var message = messageService.createMessage(
                sendMessageDTO,
                accountWsService.getAccount().getId()
        );
        var usersToNotificate = this.getMessageUsers(message);

        this.notificateUsers(
                MESSAGE,
                MessageDTO.parseDTO(message),
                usersToNotificate
        );

        sendMessageDTO.setIsSent(true);
        return sendMessageDTO;
    }

    public void readMessage(ObjectId messageId) {
        var message = messageService.readMessage(
                messageId,
                accountWsService.getAccount()
        );
        var usersToNotificate = new HashSet<>(
                this.getMessageUsers(message)
        );
        CollectionUtils.addIgnoreNull(
                usersToNotificate,
                userRepository.findById(message.getSentBy().getUserId()).orElse(null)
        );

        var readMessageDTO = this.createReadMessageDTO(message);
        this.notificateUsers(
                READ_MESSAGE,
                readMessageDTO,
                usersToNotificate
        );
    }

    private ReadMessageDTO createReadMessageDTO(Message message) {
        var readMessageDTO = new ReadMessageDTO(
                message.getId().toString()
        );
        List<ReadMessageUserDTO> readMessageUserDTOs = message.getReadBy()
                .stream()
                .map(ReadMessageUserDTO::parseDTO)
                .collect(Collectors.toList());
        readMessageDTO.setReadMessageUserDTOs(readMessageUserDTOs);

        return readMessageDTO;
    }

    private void notificateUsers(String destination,
                                 Object dto,
                                 Collection<User> users) {
        for (User user : users) {
            super.sendToAnotherUser(
                    destination,
                    dto,
                    user.getUsername()
            );
        }
    }

}
