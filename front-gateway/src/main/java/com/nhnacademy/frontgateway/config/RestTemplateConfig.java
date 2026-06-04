package com.nhnacademy.frontgateway.config;

import com.nhnacademy.frontgateway.auth.dto.LoginResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    private static final String USER_ID_HEADER = "X-User-Id";

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginResponse loginUser) {
                request.getHeaders().set(USER_ID_HEADER, loginUser.userId());
                return execution.execute(request, body);
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpSession session = attributes.getRequest().getSession(false);
                Object context = session == null ? null : session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                if (context instanceof org.springframework.security.core.context.SecurityContext securityContext
                        && securityContext.getAuthentication() != null
                        && securityContext.getAuthentication().getPrincipal() instanceof LoginResponse loginUser) {
                    request.getHeaders().set(USER_ID_HEADER, loginUser.userId());
                }
            }

            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
