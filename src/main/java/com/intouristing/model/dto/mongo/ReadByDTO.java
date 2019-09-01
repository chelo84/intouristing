package com.intouristing.model.dto.mongo;

import com.intouristing.model.entity.mongo.ReadBy;
import lombok.*;

import java.time.LocalDate;

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

    private Long user;

    private LocalDate readAt;

    public static ReadByDTO parseDTO(ReadBy readBy) {
        if (nonNull(readBy)) {
            return ReadByDTO
                    .builder()
                    .user(readBy.getUser())
                    .readAt(readBy.getReadAt())
                    .build();
        }

        return null;
    }
}
