package com.intouristing.model.entity;

import com.intouristing.model.key.PrivateChatId;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(PrivateChatId.class)
public class PrivateChat {

    @Id
    private Long firstUser;

    @Id
    private Long secondUser;

}
