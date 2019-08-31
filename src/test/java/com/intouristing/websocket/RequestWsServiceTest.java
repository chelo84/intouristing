package com.intouristing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.exceptions.RequestNotAcceptableException;
import com.intouristing.model.dto.ErrorWsDTO;
import com.intouristing.model.dto.RequestDTO;
import com.intouristing.model.entity.User;
import com.intouristing.model.enumeration.RelationshipType;
import com.intouristing.repository.UserRepository;
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
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;

    private RequestDTO getRequestDTO(String destinationToken, String senderToken) {
        User sender = userRepository.findById(TokenService.parseToken(senderToken).getId()).get(),
                destination = userRepository.findById(TokenService.parseToken(destinationToken).getId()).get();
        return RequestDTO.parseDTO(sender, destination, RelationshipType.FRIENDSHIP);
    }

    @Test
    public void shouldSendARequestToAnotherUser() throws Exception {
        String destinationToken = super.login();
        StompSession destinationSession = super.getStompSession(destinationToken);
        DefaultStompFrameHandler destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_REQUEST, destinationStompHandler);

        String senderToken = super.anotherLogin();
        StompSession senderSession = super.getStompSession(senderToken);
        StompHeaders senderStompHeaders = super.getStompHeaders(senderToken);
        RequestDTO requestDTO = this.getRequestDTO(destinationToken, senderToken);
        senderStompHeaders.setDestination(WS + REQUEST);
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
        StompSession destinationSession = super.getStompSession(destinationToken);
        DefaultStompFrameHandler destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_REQUEST, destinationStompHandler);

        String senderToken = super.anotherLogin();
        StompSession senderSession = super.getStompSession(senderToken);
        DefaultStompFrameHandler senderStompHandler = new DefaultStompFrameHandler();
        senderSession.subscribe(USER + QUEUE_REQUEST, senderStompHandler);

        StompHeaders senderStompHeaders = super.getStompHeaders(senderToken);
        RequestDTO requestDTO = this.getRequestDTO(destinationToken, senderToken);
        senderStompHeaders.setDestination(WS + REQUEST);
        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(requestDTO).getBytes());

        StompHeaders destinationStompHeaders = super.getStompHeaders(destinationToken);
        Long requestId = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(1, SECONDS), RequestDTO.class).getId();
        destinationStompHeaders.setDestination(WS + ACCEPT_REQUEST);
        destinationSession.send(destinationStompHeaders, String.valueOf(requestId).getBytes());
        senderStompHandler.blockingQueue.poll(1, SECONDS);

        RequestDTO acceptedRequest = objectMapper.readValue(senderStompHandler.blockingQueue.poll(1, SECONDS), RequestDTO.class);
        assertNotNull(acceptedRequest);
        assertEquals(TokenService.parseToken(senderToken).getId(), acceptedRequest.getSenderId());
        assertEquals(TokenService.parseToken(destinationToken).getId(), acceptedRequest.getDestinationId());
        assertNotNull(acceptedRequest.getAcceptedAt());
    }

    @Test
    public void shouldNotBeAnAcceptableRequestBecauseAnotherUserIsTheDestination() throws Exception {
        String destinationToken = super.login();
        StompSession destinationSession = super.getStompSession(destinationToken);
        DefaultStompFrameHandler destinationStompHandler = new DefaultStompFrameHandler();
        destinationSession.subscribe(USER + QUEUE_REQUEST, destinationStompHandler);

        String senderToken = super.anotherLogin();
        StompSession senderSession = super.getStompSession(senderToken);
        DefaultStompFrameHandler senderStompHandler = new DefaultStompFrameHandler();
        senderSession.subscribe(USER + QUEUE_ERROR, senderStompHandler);

        StompHeaders senderStompHeaders = super.getStompHeaders(senderToken);
        RequestDTO requestDTO = this.getRequestDTO(destinationToken, senderToken);
        senderStompHeaders.setDestination(WS + REQUEST);
        senderSession.send(senderStompHeaders, objectMapper.writeValueAsString(requestDTO).getBytes());

        Long requestId = objectMapper.readValue(destinationStompHandler.blockingQueue.poll(1, SECONDS), RequestDTO.class).getId();
        senderStompHeaders.setDestination(WS + ACCEPT_REQUEST);
        senderSession.send(senderStompHeaders, String.valueOf(requestId).getBytes());
        var errorWsDTO = objectMapper.readValue(senderStompHandler.blockingQueue.poll(1, SECONDS), ErrorWsDTO.class);

        assertNotNull(errorWsDTO);
        assertEquals(RequestNotAcceptableException.class.getSimpleName(), errorWsDTO.getException());
    }

}
