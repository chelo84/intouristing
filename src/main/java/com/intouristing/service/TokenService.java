package com.intouristing.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.intouristing.model.entity.Account;

import static com.intouristing.security.SecurityConstants.TOKEN_PREFIX;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class TokenService {

    public static Account parseToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        Account account = convertToken(jwt);
        return account;
    }

    private static Account convertToken(DecodedJWT jwt) {
        return Account.parseAccount(jwt.getClaims());
    }

    private static DecodedJWT decodeToken(String token) {
        token = token.replace(TOKEN_PREFIX, "");

        DecodedJWT jwt = JWT.decode(token);

        return jwt;
    }

}
