package com.intouristing.service;

import com.intouristing.exceptions.ChatAlreadyExistsException;
import com.intouristing.exceptions.NotFoundException;
import com.intouristing.model.dto.ChatGroupDTO;
import com.intouristing.model.entity.ChatGroup;
import com.intouristing.model.entity.PrivateChat;
import com.intouristing.model.entity.mongo.Message;
import com.intouristing.model.enumeration.ChatGroupType;
import com.intouristing.model.key.PrivateChatId;
import com.intouristing.repository.ChatGroupRepository;
import com.intouristing.repository.PrivateChatRepository;
import com.intouristing.repository.UserRepository;
import com.intouristing.repository.mongo.MessageRepository;
import com.intouristing.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
@Service
public class ChatService extends RootService {

    private final ChatGroupRepository chatGroupRepository;
    private final PrivateChatRepository privateChatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final AccountService accountService;

    @Autowired
    public ChatService(ChatGroupRepository chatGroupRepository,
                       PrivateChatRepository privateChatRepository,
                       UserRepository userRepository,
                       MessageRepository messageRepository, AccountService accountService) {
        this.chatGroupRepository = chatGroupRepository;
        this.privateChatRepository = privateChatRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.accountService = accountService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ChatGroup createChatGroup(ChatGroupDTO chatGroupDTO, Long user) {
        ChatGroup chatGroup = ChatGroup
                .builder()
                .createdAt(LocalDateTime.now())
                .title(chatGroupDTO.getTitle())
                .createdBy(userRepository.findById(user).get())
                .type(ChatGroupType.valueOf(chatGroupDTO.getType()))
                .build();

        return chatGroupRepository.save(chatGroup);
    }

    @Transactional(readOnly = true)
    public ChatGroup findChatGroup(Long chatGroupId) {
        return chatGroupRepository.findById(chatGroupId)
                .orElseThrow(() -> new NotFoundException(ChatGroup.class, chatGroupId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long deleteChatGroup(Long chatGroupId) {
        var chatGroup = chatGroupRepository.findById(chatGroupId)
                .orElseThrow(() -> new NotFoundException(ChatGroup.class, chatGroupId));

        chatGroup.setUpdatedAt(LocalDateTime.now());
        chatGroup.setExcludedAt(LocalDateTime.now());

        return chatGroupRepository.save(chatGroup).getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PrivateChat createPrivateChat(Long firstUser, Long secondUser) {
        Optional.ofNullable(findPrivateChat(firstUser, secondUser))
                .ifPresent((e) -> {
                    throw new ChatAlreadyExistsException();
                });

        PrivateChat privateChat = PrivateChat
                .builder()
                .firstUser(Math.min(firstUser, secondUser))
                .secondUser(Math.max(firstUser, secondUser))
                .build();

        return privateChatRepository.save(privateChat);
    }

    @Transactional(readOnly = true)
    public PrivateChat findPrivateChat(Long firstUser, Long secondUser) {
        return privateChatRepository.findById(
                new PrivateChatId(Math.min(firstUser, secondUser),Math.max(firstUser, secondUser))
        ).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Message> findChatGroupMessages(Long chatGroupId,
                                               int page,
                                               int size) {
        return messageRepository.findAllByChatGroup(chatGroupId,
                PageRequest.of(
                        page,
                        size,
                        Sort.by("sentAt")
                )
        );
    }

    @Transactional(readOnly = true)
    public List<Message> findPrivateChatMessages(Long firstUser,
                                                 Long secondUser,
                                                 int page,
                                                 int size) {
        return messageRepository.findAllByPrivateChat_FirstUserAndPrivateChat_SecondUser(
                Math.min(firstUser, secondUser),
                Math.max(firstUser, secondUser),
                PageRequest.of(
                        page,
                        size,
                        Sort.by("sentAt")
                )
        );
    }

    @Transactional(readOnly = true)
    public Long countUnreadMessagesByPrivateChat(Long firstUser, Long secondUser) {
        return messageRepository.countBySentBy_UserIdNotAndReadBy_UserIdIsNotInAndPrivateChat_firstUserAndPrivateChat_SecondUser(
                accountService.getAccount().getId(),
                accountService.getAccount().getId(),
                Math.min(firstUser, secondUser),
                Math.max(firstUser, secondUser)
        ).orElse(0L);
    }

}
