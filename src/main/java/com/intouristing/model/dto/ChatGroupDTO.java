package com.intouristing.model.dto;

import com.intouristing.model.entity.ChatGroup;
import com.intouristing.model.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
public class ChatGroupDTO {

    private Long id;

    private String title;

    private String type;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime excludedAt;

    private UserDTO createdBy;

    private List<Long> users;

    public static ChatGroupDTO parseDTO(ChatGroup chatGroup) {
        if (nonNull(chatGroup)) {
            return ChatGroupDTO
                    .builder()
                    .id(chatGroup.getId())
                    .title(chatGroup.getTitle())
                    .type(chatGroup.getType().name())
                    .createdAt(chatGroup.getCreatedAt())
                    .updatedAt(chatGroup.getUpdatedAt())
                    .excludedAt(chatGroup.getExcludedAt())
                    .createdBy(Optional.ofNullable(chatGroup.getCreatedBy()).map(UserDTO::parseDTO).orElse(null))
                    .users(emptyIfNull(chatGroup.getUsers()).stream().map(User::getId).collect(Collectors.toList()))
                    .build();
        }

        return null;
    }
}
