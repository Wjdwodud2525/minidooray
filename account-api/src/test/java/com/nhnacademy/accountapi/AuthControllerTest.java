package com.nhnacademy.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.account.dto.auth.LoginRequest;
import com.nhnacademy.account.dto.auth.LoginResponse;
import com.nhnacademy.account.exception.LoginFailedException;
import com.nhnacademy.account.exception.RestGlobalExceptionHandler;
import com.nhnacademy.account.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        when(userService.login("test", "1234")).thenReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("test"))
                .andExpect(jsonPath("$.email").value("test@email.com"));

        verify(userService).login("test", "1234");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void getLoginUserFail() throws Exception {
        LoginRequest request = new LoginRequest("test", "wrong-password");

        when(userService.login("test", "wrong-password"))
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

        verify(userService, never()).login(anyString(), anyString());
    }
}
