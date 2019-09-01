package com.intouristing.model.dto.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDTO {

    private String hash;

    private Long chatGroup;

    private String text;

    private Long sendTo;

    private Boolean isGroup;

    private Boolean isSent;

}