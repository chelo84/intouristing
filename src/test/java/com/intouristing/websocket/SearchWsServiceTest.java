package com.intouristing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
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

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;

import static com.intouristing.websocket.messagemapping.SearchMessageMapping.QUEUE_SEARCH;
import static com.intouristing.websocket.messagemapping.SearchMessageMapping.SEARCH;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Marcelo Lacroix on 21/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SearchWsServiceTest extends WebSocketTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Before
    public void setup() {
        super.blockingQueue = (new LinkedBlockingDeque<>());
        stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));

        log.info(WEBSOCKET_URI);
    }

    @Test
    public void shouldReceiveUsersAfterSearch() throws Exception {
        String accessToken = super.login();
        String anotherAccessToken = super.anotherLogin();
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", accessToken);
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Authorization", accessToken);
        StompSession session = stompClient
                .connect(WEBSOCKET_URI, new WebSocketHttpHeaders(), stompHeaders, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);
        session.subscribe(USER + QUEUE_SEARCH, new DefaultStompFrameHandler());

        stompHeaders.setDestination(WS + SEARCH);
        String message = "10";
        session.send(stompHeaders, message.getBytes());

        UserDTO expectedDTO = UserDTO.parseDTO(userRepository.findByUsername(TokenService.parseToken(anotherAccessToken).getUsername()).get());

        Assert.assertEquals("{\"users\":[" + objectMapper.writeValueAsString(expectedDTO) + "]}", super.blockingQueue.poll(2, SECONDS));
    }

}
