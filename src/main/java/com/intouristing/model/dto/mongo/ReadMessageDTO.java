package com.intouristing.model.dto.mongo;

import lombok.*;

import java.util.List;

/**
 * Created by Marcelo Lacroix on 03/09/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessageDTO {

    private String messageId;

    private List<ReadMessageUserDTO> readMessageUserDTOs;

    public ReadMessageDTO(String messageId) {
        this.messageId = messageId;
    }

}
