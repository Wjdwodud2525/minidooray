package com.nhnacademy.accountapi.controller;

import com.nhnacademy.accountapi.dto.user.UserCreateRequest;
import com.nhnacademy.accountapi.dto.user.UserExistsResponse;
import com.nhnacademy.accountapi.dto.user.UserResponse;
import com.nhnacademy.accountapi.dto.user.UserUpdateRequest;
import com.nhnacademy.accountapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<UserResponse> getUsers() {
        List<UserResponse> userResponseList = userService.getUsers();
        return userResponseList;
    }
    @GetMapping("/{user_id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable(name = "user_id") String id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/{user_id}/exists")
    public ResponseEntity<UserExistsResponse> checkUserExists(@PathVariable(name = "user_id") String id) {
        return ResponseEntity.ok(userService.checkUserExists(id));
    }
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserCreateRequest request
            ){
        UserResponse userResponse = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/{user_id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable(name = "user_id") String id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse userResponse = userService.updateUser(id, request);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "user_id") String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
