package com.intouristing.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static com.intouristing.websocket.messagemapping.MessageMappings.Search.QUEUE_SEARCH;
import static com.intouristing.websocket.messagemapping.MessageMappings.Search.SEARCH;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Marcelo Lacroix on 21/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SearchWsServiceTest extends WebSocketTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldReceiveUsersAfterSearch() throws Exception {
        String accessToken = super.login();
        String anotherAccessToken = super.anotherLogin();
        StompSession session = super.getStompSession(accessToken);
        DefaultStompFrameHandler stompHandler = new DefaultStompFrameHandler();
        session.subscribe(USER + QUEUE_SEARCH, stompHandler);

        StompHeaders stompHeaders = super.getStompHeaders(accessToken);
        stompHeaders.setDestination(WS + SEARCH);
        String message = "10";
        session.send(stompHeaders, message.getBytes());

        UserDTO expectedDTO = UserDTO.parseDTO(userRepository.findByUsername(TokenService.parseToken(anotherAccessToken).getUsername()).get());

        Assert.assertEquals("{\"users\":[" + objectMapper.writeValueAsString(expectedDTO) + "]}", stompHandler.blockingQueue.poll(2, SECONDS));
    }

}
