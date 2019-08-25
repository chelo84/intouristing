package com.intouristing.service;

import com.intouristing.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PingServiceTest {

    @Autowired
    PingService pingService;

    @Test
    public void shouldReturnPongMessage() throws Exception {
        Map<String, Object> ping = (Map<String, Object>) pingService.ping();
        String pingMessage = "\n########## START OF PING MESSAGE\n" +
                String.format("Message: %s\n", ping.get("Message").toString()) +
                String.format("Date: %s\n", ping.get("Date").toString()) +
                String.format("Profile: %s\n", ping.get("Profile").toString()) +
                String.format("Name: %s\n", ping.get("Name").toString()) +
                "########## END OF PING MESSAGE";
        log.info(pingMessage);
        assertNotNull(ping);
    }

}
