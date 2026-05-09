package com.pori.auth;

import com.pori.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerAcceptsNicknameInsteadOfHandle() throws Exception {
        String email = "nickname-user@pori.dev";

        userRepository.findByEmail(email).ifPresent(userRepository::delete);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "nickname-user@pori.dev",
                                  "password": "password123",
                                  "nickname": "닉네임유저"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.results.accessToken").isString());

        var savedUser = userRepository.findByEmail(email).orElseThrow();
        assertThat(savedUser.getDisplayName()).isEqualTo("닉네임유저");
        assertThat(savedUser.getHandle()).isNotBlank();
    }
}
