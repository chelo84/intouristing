package com.intouristing.security;

import com.intouristing.Application;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.service.UserService;
import com.intouristing.service.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.intouristing.security.SecurityConstants.AUTH_HEADER_STRING;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Marcelo Lacroix on 13/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class JWTAuthTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UtilService utilService;
    @Autowired
    private UserService userService;

    @Test
    public void shouldNotAllowNotAuthenticatedRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnNewAccessToken() throws Exception {
        UserDTO userDTO = utilService.buildUserDTO();
        userService.create(userDTO);

        String jsonLoginCredentials = utilService.usernamePasswordToLoginCredentials(userDTO.getUsername(), userDTO.getPassword());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/login").content(jsonLoginCredentials))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = mvcResult.getResponse().getHeader(AUTH_HEADER_STRING);

        assertNotNull(accessToken);
        assertTrue(StringUtils.isNotEmpty(accessToken));
    }

    @Test
    public void shouldBeAuthorizedUsingAccessToken() throws Exception {
        UserDTO userDTO = utilService.buildUserDTO();
        userService.create(userDTO);

        String jsonLoginCredentials = utilService.usernamePasswordToLoginCredentials(userDTO.getUsername(), userDTO.getPassword());

        String accessToken = mockMvc.perform(MockMvcRequestBuilders.post("/login").content(jsonLoginCredentials))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader(AUTH_HEADER_STRING);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/1").header(AUTH_HEADER_STRING, accessToken))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult);
        assertNotNull(mvcResult.getResponse());
        assertEquals(mvcResult.getResponse().getStatus(), 200);
    }

}
