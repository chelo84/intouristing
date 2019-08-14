package com.intouristing.intouristing.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.intouristing.intouristing.security.Account;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

import static com.intouristing.intouristing.security.SecurityConstants.TOKEN_PREFIX;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class TokenService {

    private static AbstractMap.SimpleEntry<String, String> claimsToMap(Map.Entry<String, Claim> claim) {
        return new AbstractMap.SimpleEntry<>(claim.getKey(), claim.getValue().asString());
    }

    private static LocalDate dateToLocalDate(Date date) {
        return LocalDate.from(date.toInstant().atZone(ZoneId.of("UTC")));
    }

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
