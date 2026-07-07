package com.easyexpenses.api.integrations;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {
    private static final String EXPENSES_ENDPOINT = "/api/v1/expenses";

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldBlockUnauthenticatedAccess() throws Exception {
        mockMvc.perform(
                get(EXPENSES_ENDPOINT)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.instance").value(EXPENSES_ENDPOINT))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

//    @Test
//    void shouldAllowAccessWithValidJwt() throws Exception {
//        mockMvc.perform(
//                        get(EXPENSES_ENDPOINT)
//                                .header("Authorization", "Bearer " + accessToken)
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//    }
}
