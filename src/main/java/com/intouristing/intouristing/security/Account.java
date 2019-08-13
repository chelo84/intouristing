package com.intouristing.intouristing.security;

import com.auth0.jwt.interfaces.Claim;
import lombok.*;

import java.util.Map;
import java.util.Optional;

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

    private String name;

    private String lastName;

    private String username;

    private String email;

    public static Account parseAccount(Map<String, Claim> claims) {
        return Account
                .builder()
                .id(Optional.ofNullable(claims.get("id")).map(Claim::asLong).orElse(null))
                .name(Optional.ofNullable(claims.get("name")).map(Claim::asString).orElse(null))
                .lastName(Optional.ofNullable(claims.get("lastName")).map(Claim::asString).orElse(null))
                .username(Optional.ofNullable(claims.get("username")).map(Claim::asString).orElse(null))
                .email(Optional.ofNullable(claims.get("email")).map(Claim::asString).orElse(null))
                .build();
    }
}
