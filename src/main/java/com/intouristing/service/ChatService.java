package com.intouristing.service;

import com.intouristing.exceptions.ChatAlreadyExistsException;
import com.intouristing.exceptions.NotFoundException;
import com.intouristing.model.dto.ChatGroupDTO;
import com.intouristing.model.entity.ChatGroup;
import com.intouristing.model.entity.PrivateChat;
import com.intouristing.model.enumeration.ChatGroupType;
import com.intouristing.model.key.PrivateChatId;
import com.intouristing.repository.ChatGroupRepository;
import com.intouristing.repository.PrivateChatRepository;
import com.intouristing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
@Service
public class ChatService extends RootService {

    private final ChatGroupRepository chatGroupRepository;
    private final PrivateChatRepository privateChatRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(ChatGroupRepository chatGroupRepository, PrivateChatRepository privateChatRepository, UserRepository userRepository) {
        this.chatGroupRepository = chatGroupRepository;
        this.privateChatRepository = privateChatRepository;
        this.userRepository = userRepository;
    }

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

    public Long deleteChatGroup(Long chatGroupId) {
        var chatGroup = chatGroupRepository.findById(chatGroupId)
                .orElseThrow(() -> new NotFoundException(ChatGroup.class, chatGroupId));

        chatGroup.setUpdatedAt(LocalDateTime.now());
        chatGroup.setExcludedAt(LocalDateTime.now());

        return chatGroupRepository.save(chatGroup).getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PrivateChat createPrivateChat(Long firstUser, Long secondUser) {
        privateChatRepository.findById(new PrivateChatId(Math.min(firstUser, secondUser), Math.max(firstUser, secondUser)))
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
        return privateChatRepository.findById(new PrivateChatId(Math.min(firstUser, secondUser), Math.max(firstUser, secondUser)))
                .orElseThrow(() -> new NotFoundException(ChatGroup.class, firstUser, secondUser));
    }

}
