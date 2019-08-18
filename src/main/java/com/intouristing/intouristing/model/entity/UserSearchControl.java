package com.intouristing.intouristing.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserSearchControl {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime startedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime finishedAt;

    @OneToOne
    @MapsId
    private User user;

}
