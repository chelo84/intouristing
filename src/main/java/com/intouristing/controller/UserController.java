package com.intouristing.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.service.UserService;
import com.intouristing.service.account.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.intouristing.security.SecurityConstants.*;
import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final AccountService accountService;

    @Autowired
    public UserController(UserService userService,
                          ObjectMapper objectMapper,
                          AccountService accountService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public UserDTO find(@PathVariable Long id) {
        return UserDTO.parseDTO(userService.find(id));
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDTO) {
        return UserDTO.parseDTO(userService.create(userDTO));
    }

    @PutMapping
    public UserDTO update(@RequestBody UserDTO userDTO) {
        return UserDTO.parseDTO(userService.update(userDTO));
    }

    @PostMapping("/avatar/{id}")
    public ResponseEntity<?> setAvatar(@PathVariable Long id,
                                       @RequestPart("avatar") MultipartFile multipartFile) throws Exception {
        userService.setAvatarImage(id, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/avatar/{user}")
    public ResponseEntity<?> getAvatar(@PathVariable String user, HttpServletResponse response) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.IMAGE_JPEG)
                .body(userService.getAvatarImage(user));
    }

    @GetMapping("/token/update")
    public String updateToken(@RequestHeader("Authorization") String accessToken) {
        String username = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(accessToken.replace(TOKEN_PREFIX, ""))
                .getSubject();
        String newToken = null;
        if (nonNull(username)) {
            var user = userService.findByUsername(username);
            newToken = TOKEN_PREFIX + JWT.create()
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .withClaim("id", user.getId())
                    .withClaim("name", user.getName())
                    .withClaim("lastName", user.getLastName())
                    .withClaim("username", user.getUsername())
                    .withClaim("email", user.getEmail())
                    .sign(HMAC512(SECRET.getBytes()));
        }
        return newToken;
    }

}
