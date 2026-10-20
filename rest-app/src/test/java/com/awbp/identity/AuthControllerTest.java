package com.awbp.identity;

import com.awbp.identity.application.AuthService;
import com.awbp.identity.domain.TokenPair;
import com.awbp.config.SecurityConfig;
import com.awbp.identity.application.InvalidCredentialsException;
import com.awbp.identity.application.UserAlreadyExistsException;
import com.awbp.identity.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void givenValidRequest_whenRegister_thenCreatedWithTokens() throws Exception {
        when(authService.register("john", "John", "password123"))
                .thenReturn(new TokenPair("access-token", "refresh-token"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john",
                                  "displayName": "John",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void givenShortPassword_whenRegister_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john",
                                  "displayName": "John",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenTakenUsername_whenRegister_thenConflict() throws Exception {
        when(authService.register("john", "John", "password123"))
                .thenThrow(new UserAlreadyExistsException("john"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john",
                                  "displayName": "John",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void givenValidCredentials_whenLogin_thenOkWithTokens() throws Exception {
        when(authService.login("john", "password123"))
                .thenReturn(new TokenPair("access-token", "refresh-token"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void givenWrongCredentials_whenLogin_thenUnauthorized() throws Exception {
        when(authService.login("john", "password123"))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenBlankUsername_whenLogin_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenRefreshToken_whenRefresh_thenOkWithTokens() throws Exception {
        when(authService.refresh("refresh-token"))
                .thenReturn(new TokenPair("new-access", "new-refresh"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"));
    }

    @Test
    void givenRefreshToken_whenLogout_thenNoContent() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isNoContent());
    }
}
