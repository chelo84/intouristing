package com.intouristing.intouristing.controller;

import com.intouristing.intouristing.model.dto.UserDTO;
import com.intouristing.intouristing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@RestController
@RequestMapping("/user")
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
    public void find(@PathVariable Long id) throws Exception {
        userService.find(id);
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDTO) {
        return UserDTO.parseDTO(userService.create(userDTO));
    }

}
