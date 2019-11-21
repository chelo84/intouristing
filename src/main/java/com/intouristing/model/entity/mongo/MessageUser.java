package com.intouristing.model.entity.mongo;

import com.intouristing.model.entity.User;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Marcelo Lacroix on 20/11/2019.
 */
@Data
@Builder
public class MessageUser {

    private Long userId;

    private String name;

    private String lastName;

    public static MessageUser parseUser(User user) {
        return MessageUser.builder()
                .userId(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .build();
    }

}
