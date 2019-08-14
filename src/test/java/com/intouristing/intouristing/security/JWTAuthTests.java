package com.intouristing.intouristing.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouristing.intouristing.Application;
import com.intouristing.intouristing.model.dto.UserDTO;
import com.intouristing.intouristing.model.dto.UserPositionDTO;
import com.intouristing.intouristing.security.token.TokenService;
import com.intouristing.intouristing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Marcelo Lacroix on 13/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class JWTAuthTests {

    private final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlcm5hbWUiLCJsYXN0TmFtZSI6InRlc3RMYXN0TmFtZSIsIm5hbWUiOiJ0ZXN0TmFtZSIsImlkIjoxLCJleHAiOjE" +
            "1NjY2MDg5MTQyMzMsImVtYWlsIjoidGVzdEB0ZXN0LmNvbSIsInVzZXJuYW1lIjoidGVzdFVzZXJuYW1lIn0.Ic7kRVp7Zut3hOGNLjnZ4NLvL61sW66p_ZbTfX5HZcQ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO buildUserDTO() {
        return UserDTO
                .builder()
                .username("testUsername")
                .password("testPassword")
                .userPosition(buildUserPositionDTO())
                .email("test_email@hotmail.com")
                .name("nameTest")
                .lastName("nameLastname")
                .build();
    }

    private UserPositionDTO buildUserPositionDTO() {
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
    public void shouldNotAllowNotAuthenticatedRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldParseToken() {
        Account account = TokenService.parseToken(TOKEN);

        assertEquals(account.getId().longValue(), 1L);
        assertEquals(account.getEmail(), "test@test.com");
        assertEquals(account.getName(), "testName");
        assertEquals(account.getLastName(), "testLastName");
        assertEquals(account.getUsername(), "testUsername");
    }

    @Test
    public void shouldReturnNewAccessToken() throws Exception {
        UserDTO userDTO = this.buildUserDTO();
        userService.create(userDTO);

        String jsonLoginCredentials = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", userDTO.getUsername(), userDTO.getPassword());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/login").content(jsonLoginCredentials))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = mvcResult.getResponse().getHeader("Authorization");

        assertNotNull(accessToken);
        assertTrue(StringUtils.isNotEmpty(accessToken));
    }
}
