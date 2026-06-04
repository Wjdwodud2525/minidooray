package com.nhnacademy.frontgateway.auth.controller;

import com.nhnacademy.frontgateway.auth.client.AuthClient;
import com.nhnacademy.frontgateway.auth.dto.LoginRequest;
import com.nhnacademy.frontgateway.auth.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Objects;

@Controller
public class AuthController {

    private final AuthClient authClient;
    private final SecurityContextRepository securityContextRepository;

    @Autowired
    public AuthController(AuthClient authClient, SecurityContextRepository securityContextRepository) {
        this.authClient = authClient;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/login")
    public String login(){
        if (isLoggedIn()) {
            return "redirect:/projects";
        }
        return "login/login";
    }

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
            ){
        LoginResponse loginUser = authClient.login(loginRequest);
        if(Objects.isNull(loginUser)) {
            return "redirect:/login?error";
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context,request,response);
        return "redirect:/projects";
    }

    private boolean isLoggedIn(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
