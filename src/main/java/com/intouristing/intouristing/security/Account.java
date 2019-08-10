package com.intouristing.intouristing.security;

import lombok.*;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Long id;

    private String username;

}
