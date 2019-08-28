package com.intouristing.websocket.service;

import com.intouristing.exceptions.NotFoundException;
import com.intouristing.exceptions.RequestNotAcceptableException;
import com.intouristing.model.dto.RequestDTO;
import com.intouristing.model.entity.Request;
import com.intouristing.model.entity.User;
import com.intouristing.model.enumeration.RelationshipTypeEnum;
import com.intouristing.repository.RelationshipRepository;
import com.intouristing.repository.RequestRepository;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.RelationshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;

import static com.intouristing.websocket.messagemapping.RequestMessageMapping.REQUEST;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
@Transactional
@Slf4j
@Service
public class RequestWsService extends RootWsService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RelationshipRepository relationshipRepository;
    private final RelationshipService relationshipService;

    @Autowired
    public RequestWsService(UserRepository userRepository, RequestRepository requestRepository, RelationshipRepository relationshipRepository, RelationshipService relationshipService) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.relationshipRepository = relationshipRepository;
        this.relationshipService = relationshipService;
    }

    public Request send(RequestDTO requestDTO) {
        User destination = userRepository.findById(requestDTO.getDestination())
                .orElseThrow(() -> new NotFoundException(User.class, requestDTO.getDestination()));
        Request request = Request
                .builder()
                .sender(super.getUser())
                .destination(destination)
                .createdAt(LocalDateTime.now())
                .type(RelationshipTypeEnum.FRIENDSHIP)
                .build();

        requestRepository.save(request);
        super.sendToAnotherUser(REQUEST, RequestDTO.parseDTO(request), destination.getUsername());

        return request;
    }

    public void accept(Long requestId) {
        Optional<Request> optRequest = requestRepository.findById(requestId);
        optRequest
                .map(Request::getDestination)
                .map(User::getId)
                .filter(Predicate.isEqual(super.getUser().getId()))
                .orElseThrow(RequestNotAcceptableException::new);

        Request request = optRequest.get();
        request.setAcceptedAt(LocalDateTime.now());
        requestRepository.save(request);

        relationshipService.createFriendship(request.getSender(), request.getDestination());

        super.sendToAnotherUser(REQUEST, RequestDTO.parseDTO(request), request.getSender().getUsername());
    }

}
