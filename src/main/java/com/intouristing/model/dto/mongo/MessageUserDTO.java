package com.intouristing.model.dto.mongo;

import com.intouristing.model.entity.mongo.MessageUser;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Marcelo Lacroix on 20/11/2019.
 */
@Builder
@Data
public class MessageUserDTO {

    private Long userId;

    private String name;

    private String lastName;

    public static MessageUserDTO parseDTO(MessageUser sentBy) {
        return MessageUserDTO.builder()
                .userId(sentBy.getUserId())
                .name(sentBy.getName())
                .lastName(sentBy.getLastName())
                .build();
    }
}
