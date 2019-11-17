package com.intouristing.model.entity.mongo;

import com.intouristing.model.entity.PrivateChat;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(value = "message")
public class Message {

    @Id
    private ObjectId id;

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

    private PrivateChat privateChat;

}
