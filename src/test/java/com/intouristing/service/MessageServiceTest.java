package com.intouristing.service;

import com.intouristing.Application;
import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.model.entity.ChatGroup;
import com.intouristing.model.entity.PrivateChat;
import com.intouristing.model.entity.User;
import com.intouristing.repository.ChatGroupRepository;
import com.intouristing.repository.mongo.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MessageServiceTest {

    private static final String MESSAGE_TEXT = "My message text";
    private final Long USER_ID = 1L;
    private final Long ANOTHER_USER_ID = 2L;
    private final Long GROUP_ID = 3L;
    @Autowired
    MessageService messageService;
    @Autowired
    MessageRepository messageRepository;
    @MockBean
    ChatGroupRepository chatGroupRepository;
    @MockBean
    ChatService chatService;

    public static SendMessageDTO getSendMessageDTO(Long userToSend, boolean isGroup, Long groupChat) {
        return SendMessageDTO
                .builder()
                .text(MESSAGE_TEXT)
                .hash(UUID.randomUUID().toString())
                .sendTo(userToSend)
                .isGroup(isGroup)
                .chatGroup(groupChat)
                .build();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void shouldCreatePrivateChatMessage() {
        SendMessageDTO sendMessageDTO = getSendMessageDTO(ANOTHER_USER_ID, false, null);
        when(chatService.findPrivateChat(USER_ID, sendMessageDTO.getSendTo())).thenReturn(PrivateChat.builder().firstUser(USER_ID).secondUser(ANOTHER_USER_ID).build());
        var message = messageService.createMessage(sendMessageDTO, USER_ID);
        message = messageRepository.findById(message.getId()).orElse(null);

        assertNotNull(message);
        assertNotNull(message.getId());
        assertEquals(message.getText(), sendMessageDTO.getText());
        assertThat(message.getSentTo(), containsInAnyOrder(sendMessageDTO.getSendTo()));
        assertEquals(message.getIsGroup(), sendMessageDTO.getIsGroup());
        assertNotNull(sendMessageDTO.getHash());
        assertNotNull(message.getPrivateChat());
        assertTrue(message.getPrivateChat().getFirstUser() < message.getPrivateChat().getSecondUser());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void shouldCreateGroupChatMessage() {
        SendMessageDTO sendMessageDTO = getSendMessageDTO(ANOTHER_USER_ID, true, GROUP_ID);

        var mockedUser = User.builder().id(ANOTHER_USER_ID).build();
        var mockedUser2 = User.builder().id(USER_ID).build();
        var mockedChatGroup = ChatGroup
                .builder()
                .id(sendMessageDTO.getChatGroup())
                .users(Arrays.asList(mockedUser, mockedUser2))
                .build();
        when(chatGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(mockedChatGroup));
        var message = messageService.createMessage(sendMessageDTO, USER_ID);
        message = messageRepository.findById(message.getId()).orElse(null);

        assertNotNull(message);
        assertNotNull(message.getId());
        assertEquals(message.getText(), sendMessageDTO.getText());
        assertThat(message.getSentTo(), containsInAnyOrder(mockedUser.getId()));
        assertEquals(Boolean.TRUE, sendMessageDTO.getIsGroup());
        assertNotNull(sendMessageDTO.getHash());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void shouldReadMessage() {
        SendMessageDTO sendMessageDTO = getSendMessageDTO(ANOTHER_USER_ID, false, null);
        when(chatService.findPrivateChat(USER_ID, sendMessageDTO.getSendTo())).thenReturn(PrivateChat.builder().firstUser(USER_ID).secondUser(ANOTHER_USER_ID).build());
        var message = messageService.createMessage(sendMessageDTO, USER_ID);
        message = messageRepository.findById(message.getId()).orElse(null);

        var readMessage = messageService.readMessage(message.getId(), ANOTHER_USER_ID);

        assertNotNull(readMessage);
        assertEquals(1, readMessage.getReadBy().size());
        assertTrue(readMessage.getReadByAll());
    }

}
