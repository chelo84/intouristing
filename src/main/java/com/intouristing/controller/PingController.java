package com.intouristing.controller;

import com.intouristing.service.PingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@RestController
@RequestMapping("/ping")
public class PingController {

    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping
    public ResponseEntity<Object> ping() throws Exception {
        try {
            return new ResponseEntity<>(pingService.ping(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("fail on status check", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
