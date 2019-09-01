package com.intouristing.service;

import com.intouristing.model.entity.Relationship;
import com.intouristing.model.entity.User;
import com.intouristing.model.enumeration.RelationshipType;
import com.intouristing.model.key.RelationshipId;
import com.intouristing.repository.RelationshipRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 28/08/19.
 */
@Slf4j
@Transactional
@Service
public class RelationshipService extends RootService {

    private final RelationshipRepository relationshipRepository;
    private final ChatService chatService;

    public RelationshipService(RelationshipRepository relationshipRepository, ChatService chatService) {
        this.relationshipRepository = relationshipRepository;
        this.chatService = chatService;
    }

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
}
