package com.intouristing.service;

import com.intouristing.Application;
import com.intouristing.model.dto.mongo.SendMessageDTO;
import com.intouristing.model.entity.ChatGroup;
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

import java.util.Collections;
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

    private final Long USER_ID = 1L;
    private final Long ANOTHER_USER_ID = 2L;
    private final Long GROUP_ID = 3L;
    private final String MESSAGE_TEXT = "My message text";

    @Autowired
    MessageService messageService;
    @Autowired
    MessageRepository messageRepository;
    @MockBean(name = "chatGroupRepository")
    ChatGroupRepository chatGroupRepository;

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void shouldCreatePrivateChatMessage() {
        SendMessageDTO sendMessageDTO = SendMessageDTO
                .builder()
                .text(MESSAGE_TEXT)
                .hash(UUID.randomUUID().toString())
                .sendTo(ANOTHER_USER_ID)
                .isGroup(false)
                .build();
        var message = messageService.createMessage(sendMessageDTO, USER_ID);
        message = messageRepository.findById(message.getId()).orElse(null);

        assertNotNull(message);
        assertNotNull(message.getId());
        assertEquals(message.getText(), sendMessageDTO.getText());
        assertThat(message.getSentTo(), containsInAnyOrder(sendMessageDTO.getSendTo()));
        assertEquals(message.getIsGroup(), sendMessageDTO.getIsGroup());
        assertNotNull(sendMessageDTO.getHash());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void shouldCreateGroupChatMessage() {
        SendMessageDTO sendMessageDTO = SendMessageDTO
                .builder()
                .text(MESSAGE_TEXT)
                .hash(UUID.randomUUID().toString())
                .chatGroup(GROUP_ID)
                .isGroup(true)
                .build();
        var mockedUser = User.builder().id(USER_ID).build();
        var mockedChatGroup = ChatGroup
                .builder()
                .id(sendMessageDTO.getChatGroup())
                .users(Collections.singletonList(mockedUser))
                .build();
        when(chatGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(mockedChatGroup));
        var message = messageService.createMessage(sendMessageDTO, USER_ID);
        message = messageRepository.findById(message.getId()).orElse(null);

        assertNotNull(message);
        assertNotNull(message.getId());
        assertEquals(message.getText(), sendMessageDTO.getText());
        assertThat(message.getSentTo(), containsInAnyOrder(mockedUser.getId()));
        assertEquals(message.getIsGroup(), sendMessageDTO.getIsGroup());
        assertNotNull(sendMessageDTO.getHash());
    }

}
