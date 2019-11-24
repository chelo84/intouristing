package com.intouristing.service;

import com.intouristing.exceptions.NotFoundException;
import com.intouristing.exceptions.NotSupposedToReadMessageException;
import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.model.entity.Account;
import com.intouristing.model.entity.ChatGroup;
import com.intouristing.model.entity.PrivateChat;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.mongo.Message;
import com.intouristing.model.entity.mongo.MessageUser;
import com.intouristing.model.entity.mongo.ReadBy;
import com.intouristing.repository.ChatGroupRepository;
import com.intouristing.repository.mongo.MessageRepository;
import com.intouristing.service.account.AccountService;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Service
public class MessageService extends RootService {

    private final ChatGroupRepository chatGroupRepository;
    private final ChatService chatService;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public MessageService(ChatGroupRepository chatGroupRepository,
                          ChatService chatService,
                          MessageRepository messageRepository,
                          UserService userRepository1, AccountService accountService) {
        this.chatGroupRepository = chatGroupRepository;
        this.chatService = chatService;
        this.messageRepository = messageRepository;
        this.userService = userRepository1;
        this.accountService = accountService;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Message createMessage(SendMessageDTO sendMessageDTO, Long sentBy) {
        Message message;
        if (sendMessageDTO.getIsGroup()) {
            message = Message
                    .builder()
                    .text(sendMessageDTO.getText())
                    .chatGroup(sendMessageDTO.getChatGroup())
                    .sentAt(LocalDateTime.now())
                    .sentBy(MessageUser.parseUser(userService.find(sentBy)))
                    .isGroup(true)
                    .build();

            var usersToSendMessage = chatGroupRepository.findById(sendMessageDTO.getChatGroup())
                    .orElseThrow(() -> new NotFoundException(
                                    ChatGroup.class,
                                    sendMessageDTO.getChatGroup()
                            )
                    )
                    .getUsers()
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            if (!usersToSendMessage.contains(sentBy)) {
                throw new RuntimeException("not.supposed.send.message.to.group");
            }

            message.setSentTo(
                    usersToSendMessage.stream()
                            .filter(id -> !id.equals(sentBy))
                            .map(userService::find)
                            .map(MessageUser::parseUser)
                            .collect(Collectors.toList())
            );
        } else {
            message = Message
                    .builder()
                    .text(sendMessageDTO.getText())
                    .sentBy(MessageUser.parseUser(userService.find(sentBy)))
                    .sentAt(LocalDateTime.now())
                    .sentTo(
                            List.of(
                                    MessageUser.parseUser(userService.find(sendMessageDTO.getSendTo()))
                            )
                    )
                    .isGroup(false)
                    .privateChat(
                            Optional.ofNullable(
                                    chatService.findPrivateChat(sentBy, sendMessageDTO.getSendTo())
                            ).orElseThrow(() -> new NotFoundException(PrivateChat.class, sentBy, sendMessageDTO.getSendTo()))
                    )
                    .build();
        }
        message.setId(new ObjectId());

        return messageRepository.save(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Message readMessage(ObjectId messageId, Account account) {
        Message message = messageRepository.findById(messageId).get();
        var sentToIds = emptyIfNull(message.getSentTo())
                .stream()
                .map(MessageUser::getUserId)
                .collect(Collectors.toList());
        var readByIds = emptyIfNull(message.getReadBy())
                .stream()
                .map(ReadBy::getUserId)
                .collect(Collectors.toList());
        if (sentToIds.contains(account.getId())
                && !readByIds.contains(account.getId())) {
            List<ReadBy> readBy = Optional.ofNullable(message.getReadBy())
                    .orElseGet(ArrayList::new);
            readBy.add(this.createReadBy(account));
            message.setReadBy(readBy);
            message.setReadByAll(
                    readBy.stream()
                            .map(ReadBy::getUserId)
                            .collect(Collectors.toList())
                            .containsAll(message.getSentTo())
            );
            return messageRepository.save(message);
        }

        throw new NotSupposedToReadMessageException();
    }

    private ReadBy createReadBy(Account account) {
        return ReadBy
                .builder()
                .userId(account.getId())
                .userName(account.getName())
                .userLastName(account.getLastName())
                .readAt(LocalDateTime.now())
                .build();
    }

    public Message getLastMessage(Long firstUser, Long secondUser) {
        return messageRepository.findFirstByPrivateChat_FirstUserAndPrivateChat_SecondUserOrderBySentAtDesc(
                firstUser,
                secondUser
        );
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<Message> findByPrivateChat(Long firstUser, Long secondUser, Pageable pageable) {
        var privateChat = chatService.findPrivateChat(firstUser, secondUser);

        this.readAllMessages(privateChat);

        return messageRepository.findAllByPrivateChat_FirstUserAndPrivateChat_SecondUser(
                privateChat.getFirstUser(),
                privateChat.getSecondUser(),
                pageable
        );
    }

    private void readAllMessages(PrivateChat privateChat) {
        var messages = messageRepository.findAllBySentBy_UserIdNotAndPrivateChat_FirstUserAndPrivateChat_SecondUser(
                accountService.getAccount().getId(),
                privateChat.getFirstUser(),
                privateChat.getSecondUser()
        );

        Function<Message, Consumer<Account>> readMessage = message -> account ->
                this.readMessage(message.getId(), account);
        messages.forEach(
                message -> {
                    try {
                        readMessage.apply(message)
                                .accept(accountService.getAccount());
                    } catch (NotSupposedToReadMessageException ignore) {

                    }
                }
        );
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Long countByPrivateChat(Long firstUser, Long secondUser) {
        var privateChat = chatService.findPrivateChat(firstUser, secondUser);
        return messageRepository.countByPrivateChat_FirstUserAndPrivateChat_SecondUser(
                privateChat.getFirstUser(),
                privateChat.getSecondUser()
        ).orElse(0L);
    }

}
