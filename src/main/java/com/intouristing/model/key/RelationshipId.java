package com.intouristing.model.key;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
@Data
@NoArgsConstructor
public class RelationshipId implements Serializable {

    private Long firstUser;

    private Long secondUser;

    public RelationshipId(Long firstUser, Long secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

}
