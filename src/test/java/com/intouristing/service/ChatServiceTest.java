package com.intouristing.service;

import com.intouristing.Application;
import com.intouristing.exceptions.ChatAlreadyExistsException;
import com.intouristing.model.dto.ChatGroupDTO;
import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.model.entity.User;
import com.intouristing.model.enumeration.ChatGroupType;
import com.intouristing.repository.ChatGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.intouristing.service.MessageServiceTest.getSendMessageDTO;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ChatServiceTest {

    private final Long USER_ID = 1L;
    private final Long ANOTHER_USER_ID = 2L;
    private final String GROUP_CHAT_TITLE = "My group chat title";

    @Autowired
    ChatService chatService;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    UtilService utilService;
    @Autowired
    ChatGroupRepository chatGroupRepository;

    public ChatGroupDTO getChatGroupDTO() {
        return ChatGroupDTO
                .builder()
                .title(GROUP_CHAT_TITLE)
                .type(ChatGroupType.GROUP_PRIVATE.name())
                .build();
    }

    @Test
    public void shouldCreateChatGroup() {
        var chatGroupDTO = this.getChatGroupDTO();

        var user = userService.create(utilService.buildUserDTO());
        var chatGroup = chatService.createChatGroup(chatGroupDTO, user.getId());

        assertNotNull(chatGroup);
    }

    @Test
    public void shouldReturnChatGroup() {
        var chatGroupDTO = this.getChatGroupDTO();
        var user = userService.create(utilService.buildUserDTO());
        var chatGroup = chatService.createChatGroup(chatGroupDTO, user.getId());

        var foundChatGroup = chatService.findChatGroup(chatGroup.getId());

        assertNotNull(foundChatGroup);
        assertEquals(chatGroup.getId(), foundChatGroup.getId());
    }

    @Test
    public void shouldDeleteChatGroup() {
        var chatGroupDTO = this.getChatGroupDTO();
        var user = userService.create(utilService.buildUserDTO());
        var chatGroup = chatService.createChatGroup(chatGroupDTO, user.getId());

        chatService.deleteChatGroup(chatGroup.getId());
        var deletedChatGroup = chatGroupRepository.findById(chatGroup.getId()).orElse(null);

        assertNotNull(deletedChatGroup);
        assertNotNull(deletedChatGroup.getExcludedAt());
    }

    @Test
    public void shouldCreatePrivateChat() {
        var firstUser = userService.create(utilService.buildUserDTO());
        var secondUser = userService.create(utilService.buildAnotherUserDTO());

        var privateChat = chatService.createPrivateChat(firstUser.getId(), secondUser.getId());

        assertNotNull(privateChat);
        assertNotNull(privateChat.getFirstUser());
        assertNotNull(privateChat.getSecondUser());
        assertTrue(privateChat.getFirstUser() < privateChat.getSecondUser());
    }

    @Test(expected = ChatAlreadyExistsException.class)
    public void shouldThrowChatAlreadyExistsExceptionIfPrivateChatAlreadyExists() {
        var firstUser = userService.create(utilService.buildUserDTO());
        var secondUser = userService.create(utilService.buildAnotherUserDTO());

        chatService.createPrivateChat(firstUser.getId(), secondUser.getId());
        chatService.createPrivateChat(secondUser.getId(), firstUser.getId());
    }

    @Test
    public void shouldReturnPrivateChat() {
        var firstUser = userService.create(utilService.buildUserDTO());
        var secondUser = userService.create(utilService.buildAnotherUserDTO());
        var privateChat = chatService.createPrivateChat(firstUser.getId(), secondUser.getId());

        var foundPrivateChat = chatService.findPrivateChat(firstUser.getId(), secondUser.getId());

        assertNotNull(foundPrivateChat);
        assertNotNull(foundPrivateChat.getFirstUser());
        assertNotNull(foundPrivateChat.getSecondUser());
        assertTrue(foundPrivateChat.getFirstUser() < foundPrivateChat.getSecondUser());
    }

    @Test
    @Transactional
    public void shouldFindChatGroupMessagesPagedOrderedByCreationDate() {
        var chatGroupDTO = this.getChatGroupDTO();
        var user = userService.create(utilService.buildUserDTO());
        var chatGroup = chatService.createChatGroup(chatGroupDTO, user.getId());
        List<User> users = new ArrayList<>();
        users.add(user);
        chatGroup.setUsers(users);
        chatGroupRepository.save(chatGroup);
        SendMessageDTO sendMessageDTO = getSendMessageDTO(ANOTHER_USER_ID, true, chatGroup.getId());
        for (int i = 0; i < 20; i++) {
            messageService.createMessage(sendMessageDTO, USER_ID);
        }

        var chatGroupMessages = chatService.findChatGroupMessages(chatGroup.getId(), 1, 5);

        assertNotNull(chatGroupMessages);
        assertEquals(5, chatGroupMessages.size());
        assertTrue(chatGroupMessages.get(0).getCreatedAt().isBefore(chatGroupMessages.get(chatGroupMessages.size() - 1).getCreatedAt()));
    }

    @Test
    @Transactional
    public void shouldFindPrivateChatMessagesPagedOrderedByCreationDate() {
        SendMessageDTO sendMessageDTO = getSendMessageDTO(ANOTHER_USER_ID, false, null);
        chatService.createPrivateChat(USER_ID, ANOTHER_USER_ID);

        for (int i = 0; i < 20; i++) {
            messageService.createMessage(sendMessageDTO, USER_ID);
        }

        var chatGroupMessages = chatService.findPrivateChatMessages(USER_ID, ANOTHER_USER_ID, 1, 5);

        assertNotNull(chatGroupMessages);
        assertEquals(5, chatGroupMessages.size());
        assertTrue(chatGroupMessages.get(0).getCreatedAt().isBefore(chatGroupMessages.get(chatGroupMessages.size() - 1).getCreatedAt()));
    }

}
