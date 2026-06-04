package com.nhnacademy.frontgateway.auth.client;

import com.nhnacademy.frontgateway.auth.dto.LoginRequest;
import com.nhnacademy.frontgateway.auth.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class AuthClient {
    private final RestTemplate restTemplate;

    @Value("${api.account.url}")
    private String accountUrl;

    public LoginResponse login(LoginRequest request){
        try{
            return restTemplate.postForObject(
                    UriComponentsBuilder.fromUriString(accountUrl)
                            .path("/login")
                            .toUriString(),
                    request,
                    LoginResponse.class);

        }catch(RestClientException e){
            return null;
        }
    }
}
