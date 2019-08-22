package com.intouristing.intouristing.service;

import com.intouristing.intouristing.Application;
import com.intouristing.intouristing.model.dto.UserDTO;
import com.intouristing.intouristing.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.*;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UtilService utilService;

    @Test
    public void shouldCreateNewUser() {
        UserDTO userDTO = utilService.buildUserDTO();
        User user = userService.create(userDTO);

        assertNotNull(user.getId());
        assertTrue(new BCryptPasswordEncoder().matches(userDTO.getPassword(), user.getPassword()));
        assertEquals(userDTO.getUsername(), user.getUsername());

        assertNotNull(user.getUserPosition());
        assertEquals(user.getUserPosition().getId(), user.getId());
    }

    @Test
    public void shouldFindUser() throws Exception {
        User user = userService.create(utilService.buildUserDTO());
        User foundUser = userService.find(user.getId());

        assertNotNull(foundUser);
    }

    @Test
    public void shouldSetUserAvatarImage() throws Exception {
        User user = userService.create(utilService.buildUserDTO());
        String fileName = "saber.jfif";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File file = new File(fileName);
        MultipartFile mockMultipartFile = new MockMultipartFile("file",
                file.getName(), "image/jpeg", IOUtils.toByteArray(new FileInputStream(new File(fileName)))
        );
        user = userService.setAvatarImage(user.getId(), mockMultipartFile);

        assertNotNull(user.getAvatarImage());
    }

    @Test
    public void shouldGetUserAvatarImage() throws Exception {
        User user = userService.create(utilService.buildUserDTO());

        String fileName = "saber.jfif";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File file = new File(fileName);
        MultipartFile mockMultipartFile = new MockMultipartFile("file",
                file.getName(), "image/jpeg", IOUtils.toByteArray(new FileInputStream(new File(fileName)))
        );
        user = userService.setAvatarImage(user.getId(), mockMultipartFile);

        userService.getAvatarImage(user.getId());
    }

}
