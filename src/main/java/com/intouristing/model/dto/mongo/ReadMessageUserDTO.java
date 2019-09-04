package com.intouristing.model.dto.mongo;

import com.intouristing.model.entity.User;
import com.intouristing.model.entity.mongo.ReadBy;
import lombok.*;

import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;

/**
 * Created by Marcelo Lacroix on 03/09/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessageUserDTO {

    private Long userId;

    private String userFullName;

    private LocalDateTime readAt;

    public static ReadMessageUserDTO parseDTO(ReadBy readBy, User user) {
        return ReadMessageUserDTO
                .builder()
                .userId(user.getId())
                .userFullName(String.format("%s %s", ofNullable(user.getName()).orElse(null), ofNullable(user.getLastName()).orElse(null)))
                .readAt(readBy.getReadAt())
                .build();
    }
}
