package com.intouristing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.model.dto.mongo.MessageDTO;
import com.intouristing.model.dto.mongo.ReadMessageDTO;
import com.intouristing.model.dto.mongo.ReadMessageUserDTO;
import com.intouristing.model.entity.PrivateChat;
import com.intouristing.service.ChatService;
import com.intouristing.service.MessageServiceTest;
import com.intouristing.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Collectors;

import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ChatWsServiceTest extends WebSocketTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ChatService chatService;

    @Test
    public void shouldSendANewMessage() throws Exception {
        String destinationToken = super.login();
        var destinationId = TokenService.parseToken(destinationToken).getId();
        var destinationSession = super.getStompSession(destinationToken);
        var destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_MESSAGE, destinationStompHandler);

        String senderToken = super.anotherLogin();
        var senderId = TokenService.parseToken(senderToken).getId();
        var senderSession = super.getStompSession(senderToken);
        var senderStompHeaders = super.getStompHeaders(senderToken);
        var sendMessageDTO = MessageServiceTest.getSendMessageDTO(destinationId, false, null);
        senderStompHeaders.setDestination(WS + MESSAGE);
        when(chatService.findPrivateChat(senderId, destinationId))
                .thenReturn(PrivateChat.builder().firstUser(min(senderId, destinationId)).secondUser(max(senderId, destinationId)).build());
        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(sendMessageDTO).getBytes());

        var messageDTO = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(1, SECONDS), MessageDTO.class);

        assertNotNull(messageDTO);
        assertNotNull(messageDTO.getId());
        assertEquals(sendMessageDTO.getText(), messageDTO.getText());
    }

    @Test
    public void shouldReadMessageAndNotificateUsers() throws Exception {
        String destinationToken = super.login();
        var destinationTokenParsed = TokenService.parseToken(destinationToken);
        var destinationId = destinationTokenParsed.getId();
        var destinationSession = super.getStompSession(destinationToken);
        var destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_MESSAGE, destinationStompHandler);

        String senderToken = super.anotherLogin();
        var senderTokenParsed = TokenService.parseToken(senderToken);
        var senderId = senderTokenParsed.getId();
        StompSession senderSession = super.getStompSession(senderToken);

        var senderStompHeaders = super.getStompHeaders(senderToken);
        var sendMessageDTO = MessageServiceTest.getSendMessageDTO(destinationId, false, null);
        senderStompHeaders.setDestination(WS + MESSAGE);
        when(chatService.findPrivateChat(senderId, destinationId))
                .thenReturn(PrivateChat.builder().firstUser(min(senderId, destinationId)).secondUser(max(senderId, destinationId)).build());

        var senderStompHandler = new DefaultStompFrameHandler();
        senderSession.subscribe(USER + QUEUE_READ_MESSAGE, senderStompHandler);

        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(sendMessageDTO).getBytes());

        var messageId = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(1, SECONDS), MessageDTO.class).getId();

        var destinationStompHeaders = super.getStompHeaders(destinationToken);
        destinationStompHeaders.setDestination(WS + READ_MESSAGE);
        destinationSession.send(destinationStompHeaders, messageId.getBytes());

        var readMessage = objectMapper.readValue(senderStompHandler.blockingQueue.poll(1, SECONDS), ReadMessageDTO.class);

        assertNotNull(readMessage);
        assertEquals(messageId, readMessage.getMessageId());
        assertThat(readMessage.getReadMessageUserDTOs().stream().map(ReadMessageUserDTO::getUserFullName).collect(Collectors.toList()),
                containsInAnyOrder(String.format("%s %s", destinationTokenParsed.getName(), destinationTokenParsed.getLastName())));
        assertThat(readMessage.getReadMessageUserDTOs().stream().map(ReadMessageUserDTO::getUserId).collect(Collectors.toList()),
                containsInAnyOrder(destinationId));
    }

}
