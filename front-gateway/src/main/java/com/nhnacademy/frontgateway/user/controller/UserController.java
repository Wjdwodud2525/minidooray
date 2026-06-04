package com.nhnacademy.frontgateway.user.controller;

import com.nhnacademy.frontgateway.user.client.UserClient;
import com.nhnacademy.frontgateway.user.dto.UserCreateRequest;
import com.nhnacademy.frontgateway.user.dto.UserResponse;
import com.nhnacademy.frontgateway.user.dto.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public String getUsers(Model model){
        List<UserResponse> users = userClient.getUsers();
        model.addAttribute("users", users);
        return "user/users";
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable String userId, Model model){
        UserResponse user = userClient.getUser(userId);
        model.addAttribute("user", user);
        return "user/user";
    }

    @PostMapping
    public String createUser(@ModelAttribute UserCreateRequest userCreateRequest){
        userClient.createUser(userCreateRequest);
        return "redirect:/users";
    }
    @PostMapping("/{userId}")
    public String updateUser(@ModelAttribute UserUpdateRequest userUpdateRequest, @PathVariable String userId){
        userClient.updateUser(userId, userUpdateRequest);
        return "redirect:/users";
    }
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId){
        userClient.deleteUser(userId);
        return "redirect:/users";
    }
}
