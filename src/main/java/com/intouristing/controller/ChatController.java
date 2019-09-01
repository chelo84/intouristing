package com.intouristing.controller;

import com.intouristing.model.dto.ChatGroupDTO;
import com.intouristing.model.entity.ChatGroup;
import com.intouristing.model.entity.PrivateChat;
import com.intouristing.service.ChatService;
import com.intouristing.service.account.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final AccountService accountService;
    private final ChatService chatService;

    @Autowired
    public ChatController(AccountService accountService, ChatService chatService) {
        this.accountService = accountService;
        this.chatService = chatService;
    }

    @PostMapping("/group")
    public ChatGroup createChatGroup(@RequestBody ChatGroupDTO chatGroupDTO) {
        return chatService.createChatGroup(chatGroupDTO, accountService.getAccount().getId());
    }

    @GetMapping("/group/{id}")
    public ChatGroup findChatGroup(@PathVariable Long chatGroupId) {
        return chatService.findChatGroup(chatGroupId);
    }

    @DeleteMapping("/group/{id}")
    public Long deleteChatGroup(@PathVariable Long chatGroupId) {
        return chatService.deleteChatGroup(chatGroupId);
    }

    @PostMapping("/private/{firstUser}/{secondUser}")
    public PrivateChat createPrivateChat(@PathVariable Long firstUser,
                                         @PathVariable Long secondUser) {
        return chatService.createPrivateChat(firstUser, secondUser);
    }

    @GetMapping("/private/{firstUser}/{secondUser}")
    public PrivateChat findPrivateChat(@PathVariable Long firstUser,
                                       @PathVariable Long secondUser) {
        return chatService.findPrivateChat(firstUser, secondUser);
    }

}
