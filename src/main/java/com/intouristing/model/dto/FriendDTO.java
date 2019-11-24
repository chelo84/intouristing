package com.intouristing.model.dto;

import com.intouristing.model.dto.mongo.MessageDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendDTO {

    private UserDTO user;

    private MessageDTO lastMessage;

    private Long unreadMessages;

}
