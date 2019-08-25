package com.intouristing.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Service
public class PingService extends RootService {

    private final Environment environment;

    public PingService(Environment environment) {
        this.environment = environment;
    }

    @GetMapping
    public Object ping() throws Exception {
        String profile = StringUtils.join(this.environment.getActiveProfiles());
        Map<String, Object> map = new HashMap<>();
        map.put("Message", "Pong");
        map.put("Date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        map.put("Profile", profile);
        map.put("Name", InetAddress.getLocalHost().getHostName());
        return map;
    }
}
