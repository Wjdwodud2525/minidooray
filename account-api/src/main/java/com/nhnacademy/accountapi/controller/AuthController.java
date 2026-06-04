package com.nhnacademy.accountapi.controller;

import com.nhnacademy.accountapi.dto.auth.LoginRequest;
import com.nhnacademy.accountapi.dto.auth.LoginResponse;
import com.nhnacademy.accountapi.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ){
        return userService.loginUser(request.userId(),request.password());
    }
}
