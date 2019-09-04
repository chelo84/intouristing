package com.intouristing.model.entity.mongo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadBy {

    private Long userId;

    private String userName;

    private String userLastName;

    private LocalDateTime readAt;

}
