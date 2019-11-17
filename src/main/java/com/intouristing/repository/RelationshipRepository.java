package com.intouristing.repository;

import com.intouristing.model.entity.Relationship;
import com.intouristing.model.enumeration.RelationshipType;
import com.intouristing.model.key.RelationshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
public interface RelationshipRepository extends JpaRepository<Relationship, RelationshipId> {
    @Query("select relationship from Relationship relationship " +
            "where relationship.type = ?1 " +
            "and (relationship.firstUser = ?2 " +
            "or relationship.secondUser = ?2) ")
    List<Relationship> findAllByType(RelationshipType type, Long id);
}
