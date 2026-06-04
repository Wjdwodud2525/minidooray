package com.nhnacademy.accountapi.config;

import com.nhnacademy.accountapi.entity.User;
import com.nhnacademy.accountapi.entity.UserStatus;
import com.nhnacademy.accountapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUserIfMissing("test", "test@test.com", "1234");
        createUserIfMissing("user1", "user1@test.com", "1234");
        createUserIfMissing("user2", "user2@test.com", "1234");
        createUserIfMissing("user3", "user3@test.com", "1234");
        createUserIfMissing("admin", "admin@test.com", "1234");
    }

    private void createUserIfMissing(String userId, String email, String password) {
        if (userRepository.existsById(userId)) {
            return;
        }

        ZonedDateTime now = ZonedDateTime.now();
        userRepository.save(new User(
                userId,
                passwordEncoder.encode(password),
                email,
                now,
                now,
                UserStatus.ACTIVE
        ));
    }
}
