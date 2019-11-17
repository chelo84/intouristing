package com.intouristing.model.dto;

import com.intouristing.model.dto.mongo.MessageDTO;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.mongo.Message;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendDTO {

    private UserDTO user;

    private MessageDTO lastMessage;

    public static FriendDTO parseDTO(User user, Message lastMessage) {
        return FriendDTO.builder()
                .user(UserDTO.parseDTO(user))
                .lastMessage(MessageDTO.parseDTO(lastMessage))
                .build();
    }

}
