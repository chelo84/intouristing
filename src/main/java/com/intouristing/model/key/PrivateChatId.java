package com.intouristing.model.key;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
@Data
@NoArgsConstructor
public class PrivateChatId implements Serializable {

    private Long firstUser;

    private Long secondUser;

    public PrivateChatId(Long firstUser, Long secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

}
