package com.intouristing.websocket;

import com.intouristing.model.dto.UserDTO;
import com.intouristing.model.dto.UserPositionDTO;
import com.intouristing.service.UserService;
import com.intouristing.service.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static com.intouristing.security.SecurityConstants.HEADER_STRING;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Marcelo Lacroix on 21/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class WebSocketTest {

    String WEBSOCKET_URI = "http://localhost:8080/sockjs";
    String WEBSOCKET_TOPIC = "/topic";
    String WEBSOCKET_QUEUE = "/queue";
    String WS = "/ws";
    String USER = "/user";
    WebSocketStompClient stompClient;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UtilService utilService;

    @Before
    public void setup() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));

        log.info(WEBSOCKET_URI);
    }

    @Test
    public void webSocketTest() {
        assertTrue(true);
    }

    private UserDTO buildUserDTO() {
        return UserDTO
                .builder()
                .username("testUsername")
                .password("testPassword")
                .userPosition(buildUserPositionDTO())
                .email("test_email@hotmail.com")
                .name("nameTest")
                .lastName("nameLastname")
                .build();
    }

    private UserPositionDTO buildUserPositionDTO() {
        return UserPositionDTO
                .builder()
                .accuracy(1.23456)
                .heading(22.222222)
                .latitude(33.333333)
                .longitude(21.121212)
                .speed(66.322212)
                .build();
    }

    public String login() {
        UserDTO userDTO = buildUserDTO();
        userService.create(userDTO);

        String jsonLoginCredentials = utilService.usernamePasswordToLoginCredentials(userDTO.getUsername(), userDTO.getPassword());

        try {
            return mockMvc.perform(MockMvcRequestBuilders.post("/login").content(jsonLoginCredentials))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getHeader(HEADER_STRING);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public String anotherLogin() {
        UserDTO userDTO = UserDTO
                .builder()
                .username("anotherTestUsername")
                .password("anotherTestPassword")
                .userPosition(buildUserPositionDTO())
                .email("another_test_email@hotmail.com")
                .name("anotherNameTest")
                .lastName("anotherNameLastname")
                .build();
        userService.create(userDTO);

        String jsonLoginCredentials = utilService.usernamePasswordToLoginCredentials(userDTO.getUsername(), userDTO.getPassword());

        try {
            return mockMvc.perform(MockMvcRequestBuilders.post("/login").content(jsonLoginCredentials))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getHeader(HEADER_STRING);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    class DefaultStompFrameHandler implements StompFrameHandler {

        BlockingQueue<String> blockingQueue;

        public DefaultStompFrameHandler() {
            this.blockingQueue = (new LinkedBlockingDeque<>());
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(new String((byte[]) o));
        }
    }
}
