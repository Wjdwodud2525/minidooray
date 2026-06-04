package com.nhnacademy.accountapi.service.impl;

import com.nhnacademy.accountapi.dto.auth.LoginRequest;
import com.nhnacademy.accountapi.dto.auth.LoginResponse;
import com.nhnacademy.accountapi.dto.user.UserCreateRequest;
import com.nhnacademy.accountapi.dto.user.UserExistsResponse;
import com.nhnacademy.accountapi.dto.user.UserResponse;
import com.nhnacademy.accountapi.dto.user.UserUpdateRequest;
import com.nhnacademy.accountapi.entity.User;
import com.nhnacademy.accountapi.entity.UserStatus;
import com.nhnacademy.accountapi.exception.LoginFailedException;
import com.nhnacademy.accountapi.exception.UserAlreadyExistsException;
import com.nhnacademy.accountapi.exception.UserNotFoundException;
import com.nhnacademy.accountapi.repository.UserRepository;
import com.nhnacademy.accountapi.service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse registerUser(UserCreateRequest request) {
        if(userRepository.existsByIdAndStatus(request.userId(), UserStatus.ACTIVE)) {
            throw new UserAlreadyExistsException("이미 있음");
        }
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(
                request.userId(),
                encodedPassword,
                request.email(),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                UserStatus.ACTIVE
        );
        userRepository.save(user);
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus()
        );
    }
    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAllByStatus(UserStatus.ACTIVE);
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : users) {
            userResponseList.add(new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getStatus()
            ));
        }
        return userResponseList;
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findByIdAndStatus(id,UserStatus.ACTIVE).orElseThrow(
                ()-> new UserNotFoundException("유저가 없음"));
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus()
        );
    }


    @Override
    @Transactional
    public UserResponse updateUser(String userId,  UserUpdateRequest request) {
        if(!userRepository.existsByIdAndStatus(userId, UserStatus.ACTIVE)) {
            throw new UserNotFoundException("수정할 유저가 없음");
        }
        String encodedPassword = passwordEncoder.encode(request.password());
        userRepository.updateUserByUserId(userId,request.email(),encodedPassword);
        User updatedUser = userRepository.findByIdAndStatus(userId,UserStatus.ACTIVE).orElseThrow(
                () -> new UserNotFoundException("수정한 유저를 조회못함"));
        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getStatus()
        );

    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        if(!userRepository.existsByIdAndStatus(id, UserStatus.ACTIVE)) {
            throw new UserNotFoundException("삭제할 유저가 없음");
        }
        userRepository.updateStatusByUserId(id, UserStatus.DELETED);
    }

    @Override
    @Transactional
    public LoginResponse loginUser(String id, String password) {
            User user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE).orElseThrow(
                    ()-> new LoginFailedException("아이디 또는 비밀번호가 맞지않음")
            );
            if(!passwordEncoder.matches(password, user.getPassword())) {
                throw new LoginFailedException("아이디 또는 비밀번호가 맞지않음");
            }
            userRepository.updateLastLoginAtByUserId(id, ZonedDateTime.now());
            return new LoginResponse(
                    user.getId(),
                    user.getEmail()
            );
    }
    @Override
    @Transactional
    public void convertToDormant() {
        ZonedDateTime cutoff =  ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusYears(1);
        userRepository.updateToDormant(cutoff);
    }
    @Override
    public UserExistsResponse checkUserExists(String id) {
        boolean exists = userRepository.existsById(id);
        return UserExistsResponse.builder().exists(exists).build();
    }

}
