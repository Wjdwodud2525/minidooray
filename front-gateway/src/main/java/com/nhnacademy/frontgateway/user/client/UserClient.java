package com.nhnacademy.frontgateway.user.client;

import com.nhnacademy.frontgateway.user.dto.UserCreateRequest;
import com.nhnacademy.frontgateway.user.dto.UserResponse;
import com.nhnacademy.frontgateway.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;

    @Value("${api.account.url}")
    private String accountUrl;

    public List<UserResponse> getUsers(){
        return restTemplate.exchange(
                accountUrl + "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserResponse>>() {
                }
        ).getBody();
    }
    public UserResponse getUser(String userId){
        return restTemplate.getForObject(accountUrl + "/users/" + userId, UserResponse.class);
    }
    public UserResponse createUser(UserCreateRequest userCreateRequest){
        return restTemplate.postForObject(accountUrl + "/users", userCreateRequest, UserResponse.class);
    }
    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest){
        return restTemplate.postForObject(
                accountUrl + "/users/" + userId,
                userUpdateRequest,
                UserResponse.class
        );
    }
    public void deleteUser(String userId){
        restTemplate.delete(accountUrl + "/users/" + userId);
    }
}
