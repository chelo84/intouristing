package com.intouristing.model.dto.mongo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MessagesDTO {

    List<MessageDTO> messages;

    Long limit;

}
