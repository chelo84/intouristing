package com.intouristing.controller;

import com.intouristing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Marcelo Lacroix on 15/09/2019.
 */
@Slf4j
@RestController
@RequestMapping("/verifications")
public class VerificationController {

    private final UserService userService;

    @Autowired
    public VerificationController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/username/{username}")
    public Boolean verifyUsername(@PathVariable String username) {
        return userService.verifyUsername(username);
    }

    @GetMapping("/email/{email}")
    public Boolean verifyEmail(@PathVariable String email) {
        return userService.verifyEmail(email);
    }

}
