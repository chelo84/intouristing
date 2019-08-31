package com.intouristing.repository;

import com.intouristing.model.entity.Relationship;
import com.intouristing.model.key.RelationshipId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
public interface RelationshipRepository extends JpaRepository<Relationship, RelationshipId> {
}
