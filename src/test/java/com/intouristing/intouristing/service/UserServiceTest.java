package com.intouristing.intouristing.service;

import com.intouristing.intouristing.Application;
import com.intouristing.intouristing.exceptions.NotFoundException;
import com.intouristing.intouristing.model.dto.UserDTO;
import com.intouristing.intouristing.model.dto.UserPositionDTO;
import com.intouristing.intouristing.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    private UserDTO constructUserDTO() {
        return UserDTO
                .builder()
                .username("testUsername")
                .password("testPassword")
                .userPosition(constructUserPositionDTO())
                .build();
    }

    private UserPositionDTO constructUserPositionDTO() {
        return UserPositionDTO
                .builder()
                .accuracy(1.23456)
                .heading(22.222222)
                .latitude(33.333333)
                .longitude(21.121212)
                .speed(66.322212)
                .build();
    }

    @Test
    public void shouldCreateNewUser() {
        UserDTO userDTO = constructUserDTO();
        User user = userService.create(userDTO);

        assertNotNull(user.getId());
        assertTrue(new BCryptPasswordEncoder().matches(userDTO.getPassword(), user.getPassword()));
        assertEquals(userDTO.getToken(), user.getToken());
        assertEquals(userDTO.getUsername(), user.getUsername());

        assertNotNull(user.getUserPosition());
        assertEquals(user.getUserPosition().getId(), user.getId());
    }

    @Test
    public void shouldFindUser() throws NotFoundException {
        User user = userService.create(constructUserDTO());
        User foundUser = userService.find(user.getId());

        assertNotNull(foundUser);
        assertEquals(user, foundUser);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundExceptionIfUserIsNotFound() throws NotFoundException {
        userService.find(999999L);
    }
}
