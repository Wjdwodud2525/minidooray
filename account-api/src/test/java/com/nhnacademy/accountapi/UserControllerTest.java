package com.nhnacademy.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.account.dto.user.UserCreateRequest;
import com.nhnacademy.account.dto.user.UserResponse;
import com.nhnacademy.account.dto.user.UserUpdateRequest;
import com.nhnacademy.account.entity.UserStatus;
import com.nhnacademy.account.exception.RestGlobalExceptionHandler;
import com.nhnacademy.account.exception.UserNotFoundException;
import com.nhnacademy.account.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestGlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("전체 유저 조회")
    void getAllUser() throws Exception {
        List<UserResponse> responses = List.of(
                new UserResponse("user1", "user1@email.com", UserStatus.ACTIVE),
                new UserResponse("user2", "user2@email.com", UserStatus.ACTIVE)
        );

        when(userService.getAllUser()).thenReturn(responses);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].userId").value("user2"));

        verify(userService).getAllUser();
    }

    @Test
    @DisplayName("단일 유저 조회")
    void getUser() throws Exception {
        UserResponse response = new UserResponse("test", "test@email.com", UserStatus.ACTIVE);

        when(userService.getUser("test")).thenReturn(response);

        mockMvc.perform(get("/users/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("test"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(userService).getUser("test");
    }

    @Test
    @DisplayName("단일 유저 조회 실패 - 없는 유저")
    void getUserFail() throws Exception {
        when(userService.getUser("missing-user"))
                .thenThrow(new UserNotFoundException("유저를 찾을 수 없습니다."));

        mockMvc.perform(get("/users/missing-user"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("유저 등록")
    void registerUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "new-user",
                "new-user@email.com",
                "1234"
        );
        UserResponse response = new UserResponse(
                "new-user",
                "new-user@email.com",
                UserStatus.ACTIVE
        );

        when(userService.registerUser(any(UserCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("new-user"))
                .andExpect(jsonPath("$.email").value("new-user@email.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(userService).registerUser(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("유저 등록 검증 실패 - 이메일 형식 오류")
    void registerUserValidationFail() throws Exception {
        String invalidRequest = """
                {
                  "userId": "new-user",
                  "email": "invalid-email",
                  "password": "1234"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("잘못된 요청입니다."));

        verify(userService, never()).registerUser(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("유저 수정")
    void updateUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest(
                "updated@email.com",
                "new-password"
        );
        UserResponse response = new UserResponse(
                "test",
                "updated@email.com",
                UserStatus.ACTIVE
        );

        when(userService.updateUser(anyString(), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users/test")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("test"))
                .andExpect(jsonPath("$.email").value("updated@email.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(userService).updateUser(anyString(), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("유저 삭제")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/test"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser("test");
    }
}
