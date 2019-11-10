package com.intouristing.websocket.controller;

import com.intouristing.model.dto.SearchDTO;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.model.dto.UserPositionDTO;
import com.intouristing.model.entity.UserPosition;
import com.intouristing.repository.UserPositionRepository;
import com.intouristing.service.account.AccountWsService;
import com.intouristing.websocket.service.SearchWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intouristing.websocket.messagemapping.MessageMappings.Search.*;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@RestController
@RequestMapping
public class SearchWsController extends RootWsController {

    private final SearchWsService searchWsService;
    private final AccountWsService accountWsService;
    private final UserPositionRepository userPositionRepository;

    @Autowired
    public SearchWsController(SearchWsService searchWsService,
                              AccountWsService accountWsService,
                              UserPositionRepository userPositionRepository) {
        this.searchWsService = searchWsService;
        this.accountWsService = accountWsService;
        this.userPositionRepository = userPositionRepository;
    }

    @MessageMapping(SEARCH)
    @SendToUser(QUEUE_SEARCH)
    public SearchDTO search(double radius) {
        Optional<UserPosition> currUserPosition = userPositionRepository.findByUserId(accountWsService.getAccount().getId());
        assert currUserPosition.isPresent();
        List<UserDTO> users = searchWsService.search(radius)
                .stream()
                .map((foundUser) -> UserDTO.parseDTO(
                        foundUser,
                        currUserPosition.get()
                ))
                .collect(Collectors.toList());

        return SearchDTO
                .builder()
                .users(users)
                .build();
    }

    @MessageMapping(UPDATE_POSITION)
    public void updatePosition(@Payload UserPositionDTO userPositionDTO) {
        searchWsService.updatePosition(userPositionDTO);
    }
}
