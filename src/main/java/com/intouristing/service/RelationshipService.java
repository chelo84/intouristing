package com.intouristing.service;

import com.intouristing.model.dto.FriendDTO;
import com.intouristing.model.dto.mongo.MessageDTO;
import com.intouristing.model.entity.Relationship;
import com.intouristing.model.entity.User;
import com.intouristing.model.enumeration.RelationshipType;
import com.intouristing.model.key.RelationshipId;
import com.intouristing.repository.RelationshipRepository;
import com.intouristing.service.account.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;

/**
 * Created by Marcelo Lacroix on 28/08/19.
 */
@Slf4j
@Transactional
@Service
public class RelationshipService extends RootService {

    private final RelationshipRepository relationshipRepository;
    private final ChatService chatService;
    private final MessageService messageService;
    private final AccountService accountService;
    private final UserService userService;

    public RelationshipService(RelationshipRepository relationshipRepository,
                               ChatService chatService,
                               MessageService messageService,
                               AccountService accountService,
                               UserService userService) {
        this.relationshipRepository = relationshipRepository;
        this.chatService = chatService;
        this.messageService = messageService;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Relationship createFriendship(User firstUser, User secondUser) {
        RelationshipId relationshipId = this.createRelationshipId(firstUser.getId(), secondUser.getId());
        Relationship relationship = Relationship
                .builder()
                .firstUser(relationshipId.getFirstUser())
                .secondUser(relationshipId.getSecondUser())
                .createdAt(LocalDateTime.now())
                .type(RelationshipType.FRIENDSHIP)
                .build();

        chatService.createPrivateChat(relationshipId.getFirstUser(), relationshipId.getSecondUser());

        return relationshipRepository.save(relationship);
    }

    RelationshipId createRelationshipId(Long firstUser, Long secondUser) {
        return new RelationshipId(Math.min(firstUser, secondUser), Math.max(firstUser, secondUser));
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<FriendDTO> findFriends(int page, int limit) {
        var relationships = relationshipRepository.findAllByType(
                RelationshipType.FRIENDSHIP,
                accountService.getAccount().getId()
        );

        var friendDTOs = relationships.stream()
                .map(relationship -> {
                    var lastMessage = messageService.getLastMessage(
                            relationship.getFirstUser(),
                            relationship.getSecondUser()
                    );
                    Long userId = Optional.of(relationship.getFirstUser())
                            .filter(id -> !id.equals(accountService.getAccount().getId()))
                            .orElseGet(relationship::getSecondUser);

                    return FriendDTO.parseDTO(
                            userService.find(userId),
                            lastMessage
                    );
                })
                .sorted(
                        comparing(
                                (friendDTO -> Optional.ofNullable(friendDTO)
                                        .map(FriendDTO::getLastMessage)
                                        .map(MessageDTO::getCreatedAt)
                                        .orElse(null)),
                                nullsLast(Comparator.reverseOrder())
                        )
                )
                .collect(Collectors.toList());

        var pagedListHolder = new PagedListHolder<>(friendDTOs);
        pagedListHolder.setPageSize(limit);
        pagedListHolder.setPage(page);

        return pagedListHolder.getPageList();
    }
}
