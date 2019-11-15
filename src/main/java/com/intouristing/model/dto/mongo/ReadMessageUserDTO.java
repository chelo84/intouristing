package com.intouristing.model.dto.mongo;

import com.intouristing.model.entity.mongo.ReadBy;
import lombok.*;

import java.time.LocalDateTime;

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

    private String userName;

    private String userLastName;

    private LocalDateTime readAt;

    public static ReadMessageUserDTO parseDTO(ReadBy readBy) {
        return ReadMessageUserDTO.builder()
                .userId(readBy.getUserId())
                .userName(readBy.getUserName())
                .userLastName(readBy.getUserLastName())
                .readAt(readBy.getReadAt())
                .build();
    }
}
