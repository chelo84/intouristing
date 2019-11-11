package com.intouristing.controller;

import com.intouristing.model.dto.RequestDTO;
import com.intouristing.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/requests")
public class RequestController {

    final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<RequestDTO> findRequests() {
        return requestService.findAll()
                .stream()
                .map(RequestDTO::parseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/count")
    public long countRequests() {
        return requestService.countAll();
    }
}
