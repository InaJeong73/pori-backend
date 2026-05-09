package com.propofol.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registersLogsInAndReadsCurrentUser() throws Exception {
        RegisterPayload registerPayload = new RegisterPayload("user@example.com", "password1234", "tester");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.results.email").value("user@example.com"));

        LoginPayload loginPayload = new LoginPayload("user@example.com", "password1234");

        String token = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(jsonPath("$.results.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        mockMvc.perform(get("/users/me").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.email").value("user@example.com"))
                .andExpect(jsonPath("$.results.nickname").value("tester"));
    }

    @Test
    void rejectsProtectedApiWithoutToken() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("AUTH401"));
    }

    private record RegisterPayload(String email, String password, String nickname) {
    }

    private record LoginPayload(String email, String password) {
    }
}
