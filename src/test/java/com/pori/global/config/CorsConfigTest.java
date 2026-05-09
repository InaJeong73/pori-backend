package com.pori.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigTest {

    private static final String CLOUD_RUN_ORIGIN = "https://pori-be-721529338032.asia-northeast3.run.app";
    private static final String FRONTEND_ORIGIN = "https://pori-five.vercel.app";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void allowsSwaggerUiRequestsFromCloudRunOrigin() throws Exception {
        mockMvc.perform(get("/health").header(ORIGIN, CLOUD_RUN_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, CLOUD_RUN_ORIGIN));
    }

    @Test
    void allowsRequestsFromDeployedFrontendOrigin() throws Exception {
        mockMvc.perform(get("/health").header(ORIGIN, FRONTEND_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, FRONTEND_ORIGIN));
    }
}
