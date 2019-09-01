package com.intouristing.model.dto.mongo;

import com.intouristing.model.entity.mongo.Message;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String id;

    private Long chatGroup;

    private String text;

    private Long sentBy;

    private List<Long> sentTo;

    private List<ReadByDTO> readBy;

    private Boolean readByAll = false;

    private Boolean isGroup;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime excludedAt;

    public static MessageDTO parseDTO(Message message) {
        if (nonNull(message)) {
            return MessageDTO
                    .builder()
                    .id(message.getId().toString())
                    .chatGroup(message.getChatGroup())
                    .text(message.getText())
                    .sentBy(message.getSentBy())
                    .sentTo(message.getSentTo())
                    .readBy(emptyIfNull(message.getReadBy()).stream().map(ReadByDTO::parseDTO).collect(Collectors.toList()))
                    .readByAll(message.getReadByAll())
                    .isGroup(message.getIsGroup())
                    .createdAt(message.getCreatedAt())
                    .updatedAt(message.getUpdatedAt())
                    .excludedAt(message.getExcludedAt())
                    .build();
        }

        return null;
    }

}
