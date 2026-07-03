package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.RegistrationRequest;
import com.easyexpenses.api.dtos.RegistrationResponse;
import com.easyexpenses.api.security.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    private static final String REGISTRATION_ENDPOINT = "/api/v1/auth/register";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldRegisterUser() throws Exception {
        String username = "user1";
        RegistrationRequest registrationRequest = new RegistrationRequest(username, "password");
        RegistrationResponse registrationResponse = new RegistrationResponse(1L,username, "User registered successfully");
        when(authService.register(registrationRequest))
                .thenReturn(registrationResponse);


        MvcResult result = mockMvc.perform(
                        post(REGISTRATION_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))

                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody =
                result.getResponse().getContentAsString();

        RegistrationResponse actualResponse =
                objectMapper.readValue(
                        responseBody,
                        RegistrationResponse.class
                );


        assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(registrationResponse);

        verify(authService)
                .register(registrationRequest);
    }

    @Test
    void shouldReturn500UponUnexpectedError() throws Exception {
        when(authService.register(any()))
                .thenThrow(new RuntimeException("Unexpected exception"));


        mockMvc.perform(
                        post(REGISTRATION_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new RegistrationRequest("username", "password")))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred."))
                .andExpect(jsonPath("$.instance").value(REGISTRATION_ENDPOINT))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"));
    }

    @Test
    void shouldReturn400WhenNoBodyProvided() throws Exception {
        mockMvc.perform(
                        post(REGISTRATION_ENDPOINT)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Request body is missing"))
                .andExpect(jsonPath("$.instance").value(REGISTRATION_ENDPOINT))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void shouldReturn400WhenWrongBodyParametersProvided() throws Exception {
        mockMvc.perform(
                        post(REGISTRATION_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "login": "Test",
                                            "rawPasswordWrongParameter": "password"
                                        }
                                        """)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Request contains invalid fields"))
                .andExpect(jsonPath("$.errors.rawPassword").value("must not be blank"))
                .andExpect(jsonPath("$.errors.username").value("must not be blank"))
                .andExpect(jsonPath("$.instance").value(REGISTRATION_ENDPOINT))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }
}
