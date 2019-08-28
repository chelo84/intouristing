package com.intouristing.model.key;

import com.intouristing.model.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
@Data
@NoArgsConstructor
public class RelationshipId implements Serializable {

    private User firstUser;

    private User secondUser;

    public RelationshipId(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

}
