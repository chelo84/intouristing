package com.intouristing.model.dto.mongo;

import com.intouristing.model.entity.mongo.ReadBy;
import lombok.*;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadByDTO {

    private Long userId;

    private String userName;

    private String userLastName;

    private LocalDateTime readAt;

    public static ReadByDTO parseDTO(ReadBy readBy) {
        if (nonNull(readBy)) {
            return ReadByDTO
                    .builder()
                    .userId(readBy.getUserId())
                    .userName(readBy.getUserName())
                    .userLastName(readBy.getUserLastName())
                    .readAt(readBy.getReadAt())
                    .build();
        }

        return null;
    }
}
