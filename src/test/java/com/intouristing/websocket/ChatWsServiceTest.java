package com.intouristing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.model.dto.mongo.MessageDTO;
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
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.MESSAGE;
import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.QUEUE_MESSAGE;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        StompSession destinationSession = super.getStompSession(destinationToken);
        DefaultStompFrameHandler destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_MESSAGE, destinationStompHandler);

        String senderToken = super.anotherLogin();
        var senderId = TokenService.parseToken(senderToken).getId();
        StompSession senderSession = super.getStompSession(senderToken);
        StompHeaders senderStompHeaders = super.getStompHeaders(senderToken);
        var sendMessageDTO = MessageServiceTest.getSendMessageDTO(destinationId, false, null);
        senderStompHeaders.setDestination(WS + MESSAGE);
        when(chatService.findPrivateChat(senderId, destinationId))
                .thenReturn(PrivateChat.builder().firstUser(min(senderId, destinationId)).secondUser(max(senderId, destinationId)).build());
        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(sendMessageDTO).getBytes());

        var messageDTO = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(1, TimeUnit.SECONDS), MessageDTO.class);

        assertNotNull(messageDTO);
        assertNotNull(messageDTO.getId());
        assertEquals(sendMessageDTO.getText(), messageDTO.getText());
    }

}
