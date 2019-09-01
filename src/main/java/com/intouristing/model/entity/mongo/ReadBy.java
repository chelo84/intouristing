package com.intouristing.model.entity.mongo;

import lombok.*;

import java.time.LocalDate;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadBy {

    private Long user;

    private LocalDate readAt;

}
