package com.intouristing.model.entity;

import com.intouristing.model.enumeration.RelationshipType;
import com.intouristing.model.key.RelationshipId;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(RelationshipId.class)
public class Relationship {

    @Id
    private Long firstUser;

    @Id
    private Long secondUser;

    @Enumerated(EnumType.ORDINAL)
    private RelationshipType type;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
