package com.intouristing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.model.dto.RequestDTO;
import com.intouristing.model.entity.User;
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

import static com.intouristing.security.SecurityConstants.AUTH_HEADER_STRING;
import static com.intouristing.websocket.messagemapping.RequestMessageMapping.*;
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

    private RequestDTO getRequestDTO(String destinationToken, String senderToken) {
        User sender = userRepository.findById(TokenService.parseToken(senderToken).getId()).get(),
                destination = userRepository.findById(TokenService.parseToken(destinationToken).getId()).get();
        return RequestDTO.parseDTO(sender, destination, RelationshipTypeEnum.FRIENDSHIP);
    }

    @Test
    public void shouldSendARequestToAnotherUser() throws Exception {
        String destinationToken = super.login();
        StompHeaders destinationStompHeaders = new StompHeaders();
        destinationStompHeaders.add(AUTH_HEADER_STRING, destinationToken);
        WebSocketHttpHeaders destinationWsHttpHeaders = new WebSocketHttpHeaders();
        destinationWsHttpHeaders.add(AUTH_HEADER_STRING, destinationToken);
        StompSession destinationSession = stompClient
                .connect(WEBSOCKET_URI, destinationWsHttpHeaders, destinationStompHeaders, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);
        DefaultStompFrameHandler destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_REQUEST, destinationStompHandler);

        String senderToken = super.anotherLogin();
        StompHeaders senderStompHeaders = new StompHeaders();
        senderStompHeaders.add(AUTH_HEADER_STRING, senderToken);
        WebSocketHttpHeaders senderWsHttpHeaders = new WebSocketHttpHeaders();
        senderStompHeaders.add(AUTH_HEADER_STRING, senderToken);
        StompSession senderSession = anotherStompClient
                .connect(WEBSOCKET_URI, senderWsHttpHeaders, senderStompHeaders, new StompSessionHandlerAdapter() {
                })
                .get(10, SECONDS);

        senderStompHeaders.setDestination(WS + REQUEST);
        RequestDTO requestDTO = this.getRequestDTO(destinationToken, senderToken);
        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(requestDTO).getBytes());

        RequestDTO receivedRequest = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(1, SECONDS), RequestDTO.class);
        assertNotNull(receivedRequest);
        assertNotNull(receivedRequest.getCreatedAt());
        assertEquals(TokenService.parseToken(senderToken).getId(), receivedRequest.getSenderId());
        assertEquals(TokenService.parseToken(destinationToken).getId(), receivedRequest.getDestinationId());
    }

    @Test
    public void shouldAcceptRequestAndInformUsers() throws Exception {
        String destinationToken = super.login();
        StompHeaders destinationStompHeaders = new StompHeaders();
        destinationStompHeaders.add(AUTH_HEADER_STRING, destinationToken);
        WebSocketHttpHeaders destinationWsHttpHeaders = new WebSocketHttpHeaders();
        destinationWsHttpHeaders.add(AUTH_HEADER_STRING, destinationToken);
        StompSession destinationSession = stompClient
                .connect(WEBSOCKET_URI, destinationWsHttpHeaders, destinationStompHeaders, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);
        DefaultStompFrameHandler destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_REQUEST, destinationStompHandler);

        String senderToken = super.anotherLogin();
        StompHeaders senderStompHeaders = new StompHeaders();
        senderStompHeaders.add(AUTH_HEADER_STRING, senderToken);
        WebSocketHttpHeaders senderWsHttpHeaders = new WebSocketHttpHeaders();
        senderWsHttpHeaders.add(AUTH_HEADER_STRING, senderToken);
        StompSession senderSession = anotherStompClient
                .connect(WEBSOCKET_URI, senderWsHttpHeaders, senderStompHeaders, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);
        DefaultStompFrameHandler senderStompHandler = new DefaultStompFrameHandler();
        senderSession.subscribe(USER + QUEUE_REQUEST, senderStompHandler);

        senderStompHeaders.setDestination(WS + REQUEST);
        RequestDTO requestDTO = this.getRequestDTO(destinationToken, senderToken);
        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(requestDTO).getBytes());

        destinationStompHeaders.setDestination(WS + ACCEPT_REQUEST);
        Long requestId = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(1, SECONDS), RequestDTO.class).getId();
        destinationSession.send(destinationStompHeaders, String.valueOf(requestId).getBytes());
        senderStompHandler.blockingQueue.poll(1, SECONDS);

        String poll = senderStompHandler.blockingQueue.poll(1, SECONDS);
        RequestDTO acceptedRequest = objectMapper.readValue(poll, RequestDTO.class);
        assertNotNull(acceptedRequest);
        assertEquals(TokenService.parseToken(senderToken).getId(), acceptedRequest.getSenderId());
        assertEquals(TokenService.parseToken(destinationToken).getId(), acceptedRequest.getDestinationId());
        assertNotNull(acceptedRequest.getAcceptedAt());
    }
}
