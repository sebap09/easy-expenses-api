package com.easyexpenses.api.integrations;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.dtos.*;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.repositories.UserRepository;
import com.easyexpenses.api.security.GeneratedJwtToken;
import com.easyexpenses.api.security.JwtGenerationRequest;
import com.easyexpenses.api.security.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {
    private static final String AUTH_ENDPOINT = "/api/v1/auth";
    private static final String EXPENSES_ENDPOINT = "/api/v1/expenses";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private static final String RAW_PASSWORD = "password";

    @BeforeEach
    void setup() {
        testUser = userRepository.save(
                new UserBuilder()
                        .password(passwordEncoder.encode(RAW_PASSWORD))
                        .build()
        );

    }

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

    @Test
    void shouldAllowAccessWithValidJwt() throws Exception {
        JwtGenerationRequest jwtGenerationRequest = new JwtGenerationRequest(testUser.getId().toString());
        GeneratedJwtToken token = jwtService.generateToken(jwtGenerationRequest);
        accessExpensesAndVerify200Status(token.token());
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        loginAndVerifyAuthResponse(testUser.getUsername());
    }
    @Test
    void shouldRejectWrongPassword() throws Exception {
        rejectAndVerifyBadCredentialsResponse(testUser.getUsername(), "wrong");
    }

    @Test
    void shouldRejectUnknownUser() throws Exception {
        rejectAndVerifyBadCredentialsResponse("NotKnown", RAW_PASSWORD);
    }

    @Test
    void shouldRejectMalformedJwt() throws Exception {
        JwtGenerationRequest jwtGenerationRequest = new JwtGenerationRequest(testUser.getId().toString());
        GeneratedJwtToken token = jwtService.generateToken(jwtGenerationRequest);
        mockMvc.perform(
                        get(EXPENSES_ENDPOINT)
                                .header("Authorization", "Bearer " + token.token() + "Malformed")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Invalid auth token provided!"))
                .andExpect(jsonPath("$.instance").value(EXPENSES_ENDPOINT))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    @Test
    void shouldLoginAndAccessProtectedEndpoint() throws Exception {
        AuthResponse authResponse = loginAndVerifyAuthResponse(testUser.getUsername());

        accessExpensesAndVerify200Status(authResponse.accessToken());
    }

    @Test
    void shouldLoginAndRefreshToken() throws Exception {
        AuthResponse authResponse = loginAndVerifyAuthResponse(testUser.getUsername());

        refreshTokenAndVerify200Status(authResponse.refreshToken());
    }

    @Test
    void shouldLoginAndRevokeToken() throws Exception {
        AuthResponse authResponse = loginAndVerifyAuthResponse(testUser.getUsername());

        revokeAndVerify204Status(authResponse.refreshToken());
    }

    @Test
    void shouldNotAllowTokenRefreshAfterRevoke() throws Exception {
        AuthResponse authResponse = loginAndVerifyAuthResponse(testUser.getUsername());

        revokeAndVerify204Status(authResponse.refreshToken());

        verifyRefreshTokenNoLongerValid(authResponse.refreshToken());
    }

    @Test
    void shouldRevokeOldOneAfterRefresh() throws Exception {
        AuthResponse authResponse = loginAndVerifyAuthResponse(testUser.getUsername());

        refreshTokenAndVerify200Status(authResponse.refreshToken());
        verifyRefreshTokenNoLongerValid(authResponse.refreshToken());
    }

    @Test
    void shouldVerifyRegistrationLoginExpenseRefreshRevokeButNotAllowRevokedTokens() throws Exception {
        String username = "registration";
        //registration
        RegistrationRequest registrationRequest = new RegistrationRequest(username,RAW_PASSWORD);
        mockMvc.perform(
                        post(AUTH_ENDPOINT + "/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isCreated());

        //login
        AuthResponse authResponse = loginAndVerifyAuthResponse(username);

        //check expenses access
        accessExpensesAndVerify200Status(authResponse.accessToken());

        //refresh token and revoke 1st one
        MvcResult refreshResult = refreshTokenAndVerify200Status(authResponse.refreshToken());

        RefreshResponse refreshResponse = objectMapper.readValue(
                refreshResult.getResponse().getContentAsString(),
                RefreshResponse.class);

        //1st refresh token is no longer valid
        verifyRefreshTokenNoLongerValid(authResponse.refreshToken());

        //revoke 2nd token
        revokeAndVerify204Status(refreshResponse.refreshToken());

        //2nd token is no longer valid
        verifyRefreshTokenNoLongerValid(refreshResponse.refreshToken());
    }

    private AuthResponse loginAndVerifyAuthResponse(String username) throws Exception {
        MvcResult loginResult = mockMvc.perform(
                        post(AUTH_ENDPOINT + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new AuthRequest(username, SecurityIntegrationTest.RAW_PASSWORD)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        return objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponse.class);
    }

    private void rejectAndVerifyBadCredentialsResponse(String username, String password) throws Exception {
        mockMvc.perform(
                        post(AUTH_ENDPOINT + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new AuthRequest(username, password)))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Bad credentials"))
                .andExpect(jsonPath("$.instance").value(AUTH_ENDPOINT + "/login"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    private void revokeAndVerify204Status(String refreshToken) throws Exception {
        mockMvc.perform(
                        post(AUTH_ENDPOINT + "/revoke")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new RevokeRequest(refreshToken)))
                )
                .andExpect(status().isNoContent());
    }

    private void accessExpensesAndVerify200Status(String accessToken) throws Exception {
        mockMvc.perform(
                        get(EXPENSES_ENDPOINT)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private void verifyRefreshTokenNoLongerValid(String refreshToken) throws Exception {
        mockMvc.perform(
                        post(AUTH_ENDPOINT + "/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new RefreshRequest(refreshToken)))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Invalid refresh token"))
                .andExpect(jsonPath("$.instance").value(AUTH_ENDPOINT + "/refresh"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    private MvcResult refreshTokenAndVerify200Status(String refreshToken) throws Exception {
        return mockMvc.perform(
                        post(AUTH_ENDPOINT + "/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new RefreshRequest(refreshToken)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();
    }
}
