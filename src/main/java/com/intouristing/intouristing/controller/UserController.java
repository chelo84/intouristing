package com.intouristing.intouristing.controller;

import com.intouristing.intouristing.model.dto.UserDTO;
import com.intouristing.intouristing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/token")
    public String createToken(@RequestBody UserDTO userDTO) {
        return null;
    }

    @GetMapping("/{id}")
    public void find(@PathVariable Long id) {
        userService.find(id);
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDTO) {
        return UserDTO.parseDTO(userService.create(userDTO));
    }

    @PostMapping("/avatar/{id}")
    public ResponseEntity<?> setAvatar(@PathVariable Long id,
                                       @RequestPart("avatar") MultipartFile multipartFile) throws Exception {
        userService.setAvatarImage(id, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/avatar/{id}")
    public ResponseEntity<?> getAvatar(@PathVariable Long id, HttpServletResponse response) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.IMAGE_JPEG)
                .body(userService.getAvatarImage(id));
    }
}
