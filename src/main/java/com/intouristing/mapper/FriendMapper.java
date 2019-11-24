package com.intouristing.mapper;

import com.intouristing.model.dto.FriendDTO;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.model.dto.mongo.MessageDTO;
import com.intouristing.model.entity.Relationship;
import com.intouristing.service.ChatService;
import com.intouristing.service.MessageService;
import com.intouristing.service.UserService;
import com.intouristing.service.account.AccountService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FriendMapper {

    private final MessageService messageService;
    private final AccountService accountService;
    private final UserService userService;
    private final ChatService chatService;

    public FriendMapper(MessageService messageService,
                        AccountService accountService,
                        UserService userService, ChatService chatService) {
        this.messageService = messageService;
        this.accountService = accountService;
        this.userService = userService;
        this.chatService = chatService;
    }

    public FriendDTO mapFriendDTO(Relationship relationship) {
        var lastMessage = messageService.getLastMessage(
                relationship.getFirstUser(),
                relationship.getSecondUser()
        );
        Long userId = Optional.of(relationship.getFirstUser())
                .filter(id -> !id.equals(accountService.getAccount().getId()))
                .orElseGet(relationship::getSecondUser);

        return FriendDTO.builder()
                .user(
                        UserDTO.parseDTO(userService.find(userId))
                )
                .lastMessage(
                        MessageDTO.parseDTO(lastMessage)
                )
                .unreadMessages(
                        chatService.countUnreadMessagesByPrivateChat(
                                relationship.getFirstUser(),
                                relationship.getSecondUser()
                        )
                )
                .build();
    }

}
