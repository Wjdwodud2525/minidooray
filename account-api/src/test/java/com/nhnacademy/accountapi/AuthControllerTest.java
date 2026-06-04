package com.nhnacademy.accountapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.accountapi.controller.AuthController;
import com.nhnacademy.accountapi.dto.auth.LoginRequest;
import com.nhnacademy.accountapi.dto.auth.LoginResponse;
import com.nhnacademy.accountapi.exception.LoginFailedException;
import com.nhnacademy.accountapi.exception.RestGlobalExceptionHandler;
import com.nhnacademy.accountapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestGlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("로그인 성공")
    void getLoginUser() throws Exception {
        LoginRequest request = new LoginRequest("test", "1234");
        LoginResponse response = new LoginResponse("test", "test@email.com");

        when(userService.loginUser("test", "1234")).thenReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("test"))
                .andExpect(jsonPath("$.email").value("test@email.com"));

        verify(userService).loginUser("test", "1234");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void getLoginUserFail() throws Exception {
        LoginRequest request = new LoginRequest("test", "wrong-password");

        when(userService.loginUser("test", "wrong-password"))
                .thenThrow(new LoginFailedException("로그인 실패"));

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("로그인 실패"));
    }

    @Test
    @DisplayName("로그인 요청 검증 실패 - userId 누락")
    void getLoginUserValidationFail() throws Exception {
        String invalidRequest = """
                {
                  "userId": "",
                  "password": "1234"
                }
                """;

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("잘못된 요청입니다."));

        verify(userService, never()).loginUser(anyString(), anyString());
    }
}
