package com.nhnacademy.accountapi.service;

import com.nhnacademy.accountapi.dto.auth.LoginRequest;
import com.nhnacademy.accountapi.dto.auth.LoginResponse;
import com.nhnacademy.accountapi.dto.user.UserCreateRequest;
import com.nhnacademy.accountapi.dto.user.UserExistsResponse;
import com.nhnacademy.accountapi.dto.user.UserResponse;
import com.nhnacademy.accountapi.dto.user.UserUpdateRequest;
import com.nhnacademy.accountapi.entity.User;

import java.util.List;

public interface UserService {
    UserResponse registerUser(UserCreateRequest request);
    LoginResponse loginUser(String id, String password);
    UserResponse updateUser(String userId, UserUpdateRequest request);
    void deleteUser(String id);
    List<UserResponse> getUsers();
    UserResponse getUserById(String id);
    void convertToDormant();
    UserExistsResponse checkUserExists(String id);
}
