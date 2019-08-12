package com.intouristing.intouristing.security.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.intouristing.security.Account;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public class TokenService {

    final static private String TOKEN_PREFIX = "BEARER";

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
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> claimsMap = jwt.getClaims()
                .entrySet()
                .stream()
                .filter(map -> !map.getKey().equals("exp"))
                .map(TokenService::claimsToMap)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return objectMapper.convertValue(claimsMap, Account.class);
    }

    private static DecodedJWT decodeToken(String token) {
        token = token.replace(TOKEN_PREFIX, "");

        DecodedJWT jwt;
        jwt = JWT.decode(token);

        return jwt;
    }

}
