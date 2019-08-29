package com.intouristing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.model.dto.RequestDTO;
import com.intouristing.model.enumeration.RelationshipTypeEnum;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Collections;

import static com.intouristing.websocket.messagemapping.RequestMessageMapping.QUEUE_REQUEST;
import static com.intouristing.websocket.messagemapping.RequestMessageMapping.REQUEST;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Marcelo Lacroix on 28/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestWsServiceTest extends WebSocketTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldSendARequestToAnotherUser() throws Exception {
        String destinationToken = super.login();
        StompHeaders destinationStompHeaders = new StompHeaders();
        destinationStompHeaders.add("Authorization", destinationToken);
        WebSocketHttpHeaders destinationWsHttpHeaders = new WebSocketHttpHeaders();
        destinationWsHttpHeaders.add("Authorization", destinationToken);
        StompSession destinationSession = stompClient
                .connect(WEBSOCKET_URI, destinationWsHttpHeaders, destinationStompHeaders, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);
        DefaultStompFrameHandler destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_REQUEST, destinationStompHandler);

        WebSocketStompClient senderStompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
        String senderToken = super.anotherLogin();
        StompHeaders senderStompHeaders = new StompHeaders();
        senderStompHeaders.add("Authorization", senderToken);
        WebSocketHttpHeaders senderWsHttpHeaders = new WebSocketHttpHeaders();
        senderStompHeaders.add("Authorization", senderToken);
        StompSession senderSession = senderStompClient
                .connect(WEBSOCKET_URI, senderWsHttpHeaders, senderStompHeaders, new StompSessionHandlerAdapter() {
                })
                .get(10, SECONDS);

        senderStompHeaders.setDestination(WS + REQUEST);
        RequestDTO requestDTO = RequestDTO
                .builder()
                .sender(TokenService.parseToken(senderToken).getId())
                .destination(TokenService.parseToken(destinationToken).getId())
                .type(RelationshipTypeEnum.FRIENDSHIP.name())
                .build();
        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(requestDTO).getBytes());

        RequestDTO receivedRequest = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(2, SECONDS), RequestDTO.class);
        assertNotNull(receivedRequest);
        assertEquals(TokenService.parseToken(senderToken).getId(), receivedRequest.getSender());
        assertEquals(TokenService.parseToken(destinationToken).getId(), receivedRequest.getDestination());
    }
}
