package com.intouristing.controller;

import com.intouristing.model.dto.FriendDTO;
import com.intouristing.service.RelationshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/relationships")
public class RelationshipController {

    private final RelationshipService relationshipService;

    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    @GetMapping("/friends")
    public List<FriendDTO> findFriends(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return relationshipService.findFriends(page, limit);
    }

}
