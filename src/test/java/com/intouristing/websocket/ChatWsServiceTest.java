package com.intouristing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.model.dto.mongo.MessageDTO;
import com.intouristing.service.MessageServiceTest;
import com.intouristing.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.MESSAGE;
import static com.intouristing.websocket.messagemapping.MessageMappings.Chat.QUEUE_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ChatWsServiceTest extends WebSocketTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldSendANewMessage() throws Exception {
        String destinationToken = super.login();
        StompSession destinationSession = super.getStompSession(destinationToken);
        DefaultStompFrameHandler destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_MESSAGE, destinationStompHandler);

        String senderToken = super.anotherLogin();
        StompSession senderSession = super.getStompSession(senderToken);
        StompHeaders senderStompHeaders = super.getStompHeaders(senderToken);
        var sendMessageDTO = MessageServiceTest.getSendMessageDTO(TokenService.parseToken(destinationToken).getId(), false, null);
        senderStompHeaders.setDestination(WS + MESSAGE);
        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(sendMessageDTO).getBytes());
        var messageDTO = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(1, TimeUnit.SECONDS), MessageDTO.class);

        assertNotNull(messageDTO);
        assertNotNull(messageDTO.getId());
        assertEquals(sendMessageDTO.getText(), messageDTO.getText());
    }

}
