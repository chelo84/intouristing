package com.intouristing.controller;

import com.intouristing.model.dto.ChatGroupDTO;
import com.intouristing.model.dto.mongo.MessageDTO;
import com.intouristing.model.entity.PrivateChat;
import com.intouristing.service.ChatService;
import com.intouristing.service.account.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ChatGroupDTO createChatGroup(@RequestBody ChatGroupDTO chatGroupDTO) {
        return ChatGroupDTO.parseDTO(
                chatService.createChatGroup(
                        chatGroupDTO,
                        accountService.getAccount().getId()
                )
        );
    }

    @GetMapping("/group/{id}")
    public ChatGroupDTO findChatGroup(@PathVariable("id") Long chatGroupId) {
        return ChatGroupDTO.parseDTO(chatService.findChatGroup(chatGroupId));
    }

    @GetMapping(value = "/group/{id}/messages", params = {"page", "size"})
    public List<MessageDTO> findChatGroupMessages(@PathVariable("id") Long chatGroupId,
                                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        return chatService.findChatGroupMessages(chatGroupId, page, size)
                .stream()
                .map(MessageDTO::parseDTO)
                .collect(Collectors.toList());
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
