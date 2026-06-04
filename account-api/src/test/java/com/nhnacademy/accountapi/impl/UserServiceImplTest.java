package com.nhnacademy.accountapi.impl;

import com.nhnacademy.accountapi.dto.auth.LoginResponse;
import com.nhnacademy.accountapi.dto.user.UserCreateRequest;
import com.nhnacademy.accountapi.dto.user.UserResponse;
import com.nhnacademy.accountapi.dto.user.UserUpdateRequest;
import com.nhnacademy.accountapi.entity.User;
import com.nhnacademy.accountapi.entity.UserStatus;
import com.nhnacademy.accountapi.exception.LoginFailedException;
import com.nhnacademy.accountapi.exception.UserAlreadyExistsException;
import com.nhnacademy.accountapi.exception.UserNotFoundException;
import com.nhnacademy.accountapi.repository.UserRepository;
import com.nhnacademy.accountapi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    //메소드에 들어가는 인자값 확인
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("회원가입 테스트")
    void registerUserTest() {
        UserCreateRequest request = new UserCreateRequest(
                "testId",
                "test@email.com",
                "1234"
        );

        when(userRepository.existsByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        UserResponse response = userService.registerUser(request);

        verify(userRepository).save(userCaptor.capture());
        User result = userCaptor.getValue();

        verify(userRepository,times(1)).existsByIdAndStatus(anyString(), eq(UserStatus.ACTIVE));

        assertAll(
                () -> assertEquals("testId", response.userId()),
                () -> assertEquals("test@email.com", response.email()),
                () -> assertEquals(UserStatus.ACTIVE, response.status()),
                () -> assertEquals("testId", result.getUserId()),
                () -> assertEquals("test@email.com", result.getEmail()),
                () -> assertEquals("encoded", result.getPassword()),
                () -> assertEquals(UserStatus.ACTIVE, result.getStatus())
        );
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 활성 유저 중복")
    void registerUserFailTest() {
        UserCreateRequest request = new UserCreateRequest(
                "testId",
                "test@email.com",
                "1234"
        );

        when(userRepository.existsByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("모든 유저 조회 테스트 - 활성화된 유저만 조회됨")
    void getAllUserTest(){
        User user = new User(
                "test",
                "1234",
                "test@email.com",
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                UserStatus.ACTIVE
        );

        when(userRepository.findAllByStatus(eq(UserStatus.ACTIVE))).thenReturn(List.of(user));

        List<UserResponse> userResponses = userService.getUsers();

        verify(userRepository, times(1)).findAllByStatus(eq(UserStatus.ACTIVE));
        assertAll(
                () -> assertEquals(1, userResponses.size()),
                () -> assertEquals(user.getUserId(), userResponses.get(0).userId()),
                () -> assertEquals(user.getEmail(), userResponses.get(0).email()),
                () -> assertEquals(user.getStatus(), userResponses.get(0).status())
        );

    }

    @Test
    @DisplayName("단일 유저 조회 테스트 - 활성화된 유저만 조회됨")
    void getUserTest(){
        User user = new User(
                "test",
                "1234",
                "test@email.com",
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                UserStatus.ACTIVE
        );
        when(userRepository.findByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(Optional.of(user));


        UserResponse response = userService.getUserById(user.getUserId());


        verify(userRepository, times(1)).findByIdAndStatus(anyString(), eq(UserStatus.ACTIVE));
        assertAll(
                () -> assertEquals(user.getEmail(), response.email()),
                () -> assertEquals(user.getStatus(),response.status()),
                () -> assertEquals(user.getUserId(), response.userId())
        );

    }

    @Test
    @DisplayName("단일 유저 조회 실패 테스트 - 활성 유저 없음")
    void getUserFailTest(){
        when(userRepository.findByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("missing-user"));
    }

    @Test
    @DisplayName("유저 업데이트 테스트")
    void updateUserTest(){
        User user = new User(
                "test",
                "1234",
                "test@email.com",
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                UserStatus.ACTIVE
        );
        UserUpdateRequest request = new UserUpdateRequest(
                "modified@email.com",
                "modifiedPassword"
        );

        User updatedUser = new User(
                "test",
                "encoded",
                "modified@email.com",
                user.getCreatedAt(),
                user.getLastLoginAt(),
                UserStatus.ACTIVE
        );

        when(userRepository.existsByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.updateUserByUserId(anyString(), anyString(), anyString())).thenReturn(1);
        when(userRepository.findByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(Optional.of(updatedUser));

        UserResponse response = userService.updateUser(user.getUserId(), request);

        verify(userRepository, times(1)).updateUserByUserId(user.getUserId(), request.email(), "encoded");
        assertAll(
                () -> assertEquals(updatedUser.getUserId(), response.userId()),
                () -> assertEquals(updatedUser.getEmail(), response.email()),
                () -> assertEquals(updatedUser.getStatus(), response.status())
        );

    }

    @Test
    @DisplayName("유저 탈퇴(비활성화)테스트")
    void deleteUserTest(){
        when(userRepository.existsByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(true);
        when(userRepository.updateStatusByUserId(anyString(), eq(UserStatus.DELETED))).thenReturn(1);

        userService.deleteUser("test");

        verify(userRepository, times(1)).updateStatusByUserId("test", UserStatus.DELETED);
    }

    @Test
    @DisplayName("로그인 테스트 - 조회되는 유저가 있는지 확인")
    void loginTest(){
        User user = new User(
                "test",
                "encoded-password",
                "test@email.com",
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                UserStatus.ACTIVE
        );

        when(userRepository.findByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "encoded-password")).thenReturn(true);
        when(userRepository.updateLastLoginAtByUserId(anyString(), any(ZonedDateTime.class))).thenReturn(1);

        LoginResponse response = userService.loginUser("test", "1234");

        verify(userRepository, times(1)).updateLastLoginAtByUserId(eq("test"), any(ZonedDateTime.class));
        assertAll(
                () -> assertEquals(user.getUserId(), response.userId()),
                () -> assertEquals(user.getEmail(), response.email())
        );
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void loginFailTest(){
        User user = new User(
                "test",
                "encoded-password",
                "test@email.com",
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                UserStatus.ACTIVE
        );

        when(userRepository.findByIdAndStatus(anyString(), eq(UserStatus.ACTIVE))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(LoginFailedException.class, () -> userService.loginUser("test", "wrong-password"));
        verify(userRepository, never()).updateLastLoginAtByUserId(anyString(), any(ZonedDateTime.class));
    }
}
