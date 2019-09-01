package com.intouristing.model.entity.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    private String id;

    private Long chatGroup;

    private String text;

    private Long sentBy;

    private List<Long> sentTo;

    private List<ReadBy> readBy;

    private Boolean readByAll = false;

    private Boolean isGroup;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime excludedAt;

}
