package com.nhnacademy.frontgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityContextRepository securityContextRepository(){
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                               SecurityContextRepository securityContextRepository) throws Exception{
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/favicon.ico", "/error").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(AbstractHttpConfigurer::disable);
        http.exceptionHandling(exceptionHandling-> exceptionHandling.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));
        http.securityContext(context->context.securityContextRepository(securityContextRepository));
        http.logout(logout->logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
        );
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
