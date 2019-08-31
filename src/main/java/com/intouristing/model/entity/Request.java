package com.intouristing.model.entity;

import com.intouristing.model.enumeration.RelationshipTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 27/08/19.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private User destination;

    @Enumerated(EnumType.ORDINAL)
    private RelationshipTypeEnum type;

    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime declinedAt;

}
