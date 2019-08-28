package com.intouristing.model.entity;

import com.intouristing.model.enumeration.RelationshipTypeEnum;
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
    private User firstUser;

    @Id
    private User secondUser;

    @Enumerated(EnumType.ORDINAL)
    private RelationshipTypeEnum type;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
